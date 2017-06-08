# 事务的传递和处理
从IPCThreadState的角度看，它的transact()函数是通过向binder驱动发出BC_TRANSACTION语义，来表达其传输意图的，而后如有必要，它会等待从binder发回的回馈，这些回馈语义常常以“BR_”开头。另一方面，当IPCThreadState作为处理命令的一方需要向发起方反馈信息的话，它会调用sendReply()函数，向binder驱动发出BC_REPLY语义。当BC_语义经由binder驱动递送到目标端时，会被binder驱动自动修改为相应的BR_语义.


当语义传递到binder驱动后，会走到binder_ioctl()函数，该函数又会调用到binder_thread_write()和binder_thread_read()。

事务的传递和处理就位于 binder_thread_write()和binder_thread_read()这两个函数中。


## BC_TRANSACTION事务（携带TF_ONE_WAY标记）的处理

携带TF_ONE_WAY标记的BC_TRANSACTION事务，这种事务是不需要回复。


### binder_transaction()

1. 找目标binder_node；
2. 找目标binder_proc；
3. 分析并插入红黑树节点；（我们在上一篇文章中已在说过这部分的机理了，只是当时没有贴出相应的代码）
4. 创建binder_transaction节点，并将其插入目标进程的todo列表；
5. 尝试唤醒目标进程。

![transaction图](http://static.oschina.net/uploads/img/201310/08224457_5I3O.png)


###  binder_thread_read()

当目标进程被唤醒时，会接着执行自己的binder_thread_read()，尝试解析并执行那些刚收来的工作。无论收来的工作来自于“binder_proc的todo链表”，还是来自于某“binder_thread的todo链表”，现在要开始从todo链表中摘节点了，而且在完成工作之后，会彻底删除binder_transaction节点。


简单说来就是，如果没有工作需要做，binder_thread_read()函数就进入睡眠或返回，否则binder_thread_read()函数会从todo队列摘下了一个节点，并把节点里的数据整理成一个binder_transaction_data结构，然后通过copy_to_user()把该结构传到用户态。因为这次传输带有TF_ONE_WAY标记，所以copy完后，只是简单地调用kfree(t)把这个binder_transaction节点干掉了。

binder_thread_read()尝试调用wait_event_interruptible()或wait_event_interruptible_exclusive()来等待待处理的工作。wait_event_interruptible()是个宏定义，和wait_event()类似，不同之处在于前者不但会判断“苏醒条件”，还会判断当前进程是否带有挂起的系统信号，当“苏醒条件”满足时（比如binder_has_thread_work(thread)返回非0值），或者有挂起的系统信号时，表示进程有工作要做了，此时wait_event_interruptible()将跳出内部的for循环。如果的确不满足跳出条件的话，wait_event_interruptible()会进入挂起状态。

另外，在调用copy_to_user()之前，binder_thread_read()先通过put_user()向上层拷贝了一个命令码，在当前的情况下，这个命令码是BR_TRANSACTION。想当初，内核态刚刚从用户态拷贝来的命令码是BC_TRANSACTION，现在要发给目标端了，就变成了BR_TRANSACTION。

## BC_TRANSACTION事务（不带TF_ONE_WAY标记）

### binder_transaction()
对于不带TF_ONE_WAY标记的BC_TRANSACTION事务来说，情况就没那么简单了。因为binder驱动不仅要找到目标进程，而且还必须努力找到一个明确的目标线程。正如我们前文所说，binder驱动希望可以充分复用目标进程中的binder工作线程。

获取目标binder_proc的部分和前一小节没什么不同，但是因为本次传输不再携带TF_ONE_WAY标记了，所以函数中会尽力去查一个合适的“目标binder_thread”，此时会用到binder_thread里的“事务栈”（transaction_stack）概念。

逻辑上说，线程节点的transaction_stack域体现了两个方面的意义：
- 这个线程需要别的线程帮它做某项工作；
- 别的线程需要这个线程做某项工作；


### binder_thread_read()
总体说来，binder_thread_read()的动作大体也就是：

1. 利用wait_event_xxxx()让自己挂起，等待下一次被唤醒； 
2. 唤醒后找到合适的待处理的工作节点，即binder_transaction节点； 
3. 把binder_transaction中的信息整理到一个binder_transaction_data中； 
4. 整理一个cmd整数值，具体数值或者为BR_TRANSACTION，或者为BR_REPLY； 
5. 将cmd数值和binder_transaction_data拷贝到用户态； 
6. 如有必要，将得到的binder_transaction节点插入目标端线程的transaction_stack堆栈中。


### 目标端如何处理传来的事务
 binder_thread_read()本身只负责读取数据，它并不解析得到的语义。具体解析语义的动作并不在内核态，而是在用户态。
 当发起端调用binder_thread_write()唤醒目标端的进程时，目标进程会从其上次调用binder_thread_read()的地方苏醒过来。辗转跳出上面的talkWithDriver()函数，并走到executeCommand()一句。
 因为binder_thread_read()中已经把BR_命令整理好了，所以executeCommand()当然会走到case BR_TRANSACTION分支。

最关键的一句当然是b->transact()啦，此时b的值来自于binder_transaction_data的cookie域，本质上等于驱动层所记录的binder_node节点的cookie域值，这个值在用户态就是BBinder指针。

在调用完transact()动作后，executeCommand()会判断tr.flags有没有携带TF_ONE_WAY标记，如果没有携带，说明这次传输是需要回复的，于是调用sendReply()进行回复。