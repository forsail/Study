分析 应用程序通过BpBinder向远端发起传输的过程

#  Binder是如何做到精确打击的
 我们先问一个问题，binder机制到底是如何从代理对象找到其对应的binder实体呢？难道它有某种制导装置吗？要回答这个问题，我们只能静下心来研究binder驱动的代码。在本系列文档的初始篇中，我们曾经介绍过ProcessState，这个结构是属于应用层次的东西，仅靠它当然无法完成精确打击。其实，在binder驱动层，还有个与之相对的结构，叫做binder_proc。


 ## 创建binder_proc
binder_proc是描述Binder进程上下文信息的结构体。
 当构造ProcessState并打开binder驱动之时，会调用到驱动层的binder_open()函数，而binder_proc就是在binder_open()函数中创建的。新创建的binder_proc会作为一个节点，插入一个总链表（binder_procs）中。
新创建的binder_proc会被记录在参数filp的private_data域中，以后每次执行binder_ioctl()，都会从filp->private_data域重新读取binder_proc的。


## binder_proc中的4棵红黑树
```c
struct binder_proc
{
    struct hlist_node proc_node;
    struct rb_root threads;
    struct rb_root nodes;
    struct rb_root refs_by_desc;
    struct rb_root refs_by_node;
    int pid;
    . . . . . .
    . . . . . .
};
```

nodes树用于记录binder实体，refs_by_desc树和refs_by_node树则用于记录binder代理。


在一个进程中，有多少“被其他进程进行跨进程调用的”binder实体，就会在该进程对应的nodes树中生成多少个红黑树节点。另一方面，一个进程要访问多少其他进程的binder实体，则必须在其refs_by_desc树中拥有对应的引用节点。

这4棵树的节点类型是不同的，threads树的节点类型为binder_thread，nodes树的节点类型为binder_node，refs_by_desc树和refs_by_node树的节点类型相同，为binder_ref。这些节点内部都会包含rb_node子结构，该结构专门负责连接节点的工作.


nodes树是用于记录binder实体的，所以nodes树中的每个binder_node节点，必须能够记录下相应binder实体的信息。因此请大家注意binder_node的ptr域和cookie域。

另一方面，refs_by_desc树和refs_by_node树的每个binder_ref节点则和上层的一个BpBinder对应，而且更重要的是，它必须具有和“目标binder实体的binder_node”进行关联的信息。

可以更深入地说明binder句柄的作用了，比如进程1的BpBinder在发起跨进程调用时，向binder驱动传入了自己记录的句柄值，binder驱动就会在“进程1对应的binder_proc结构”的引用树中查找和句柄值相符的binder_ref节点，一旦找到binder_ref节点，就可以通过该节点的node域找到对应的binder_node节点，这个目标binder_node当然是从属于进程2的binder_proc啦，不过不要紧，因为binder_ref和binder_node都处于binder驱动的地址空间中，所以是可以用指针直接指向的。目标binder_node节点的cookie域，记录的其实是进程2中BBinder的地址，binder驱动只需把这个值反映给应用层，应用层就可以直接拿到BBinder了。这就是Binder完成精确打击的大体过程。


# BpBinder和IPCThreadState
BpBinder是代理端的核心，主要负责跨进程传输，并且不关心所传输的内容。而ProcessState则是进程状态的记录器，它里面记录着打开binder驱动后得到的句柄值。
作为代理端的核心，BpBinder总要通过某种方式和binder驱动打交道，才可能完成跨进程传递语义的工作。既然binder驱动对应的句柄在ProcessState中记着，那么现在就要看BpBinder如何和ProcessState联系了。
IPCThreadState是“和跨进程通信（IPC）相关的线程状态”。一个具有多个线程的进程里应该会有多个IPCThreadState对象了，只不过每个线程只需一个IPCThreadState对象而已。这有点儿“局部单例”的意思。所以，在实际的代码中，IPCThreadState对象是存放在线程的局部存储区（TLS）里的。

## BpBinder的transact()动作
每当我们利用BpBinder的transact()函数发起一次跨进程事务时，其内部其实是调用IPCThreadState对象的transact()。

当然，进程中的一个BpBinder有可能被多个线程使用，所以发起传输的IPCThreadState对象可能并不是同一个对象，但这没有关系，因为这些IPCThreadState对象最终使用的是同一个ProcessState对象。

### 调用IPCThreadState的transact()
IPCThreadState::transact()会先调用writeTransactionData()函数将data数据整理进内部的mOut包中.
接着IPCThreadState::transact()会考虑本次发起的事务是否需要回复。“不需要等待回复的”事务，在其flag标志中会含有TF_ONE_WAY，表示一去不回头。而“需要等待回复的”，则需要在传递时提供记录回复信息的Parcel对象，一般发起transact()的用户会提供这个Parcel对象，如果不提供，transact()函数内部会临时构造一个假的Parcel对象。
实际完成跨进程事务的是waitForResponse()函数.

### talkWithDriver
waitForResponse()中是通过调用talkWithDriver()来和binder驱动打交道的，说到底会调用ioctl()函数。因为ioctl()函数在传递BINDER_WRITE_READ语义时，既会使用“输入buffer”，也会使用“输出buffer”，所以IPCThreadState专门搞了两个Parcel类型的成员变量：mIn和mOut。总之就是，mOut中的内容发出去，发送后的回复写进mIn。