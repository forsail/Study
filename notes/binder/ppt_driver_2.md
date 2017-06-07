# 底层IPC机制
在驱动层数据又是如何传递到BBinder一侧的呢？上一篇文章虽然阐述了4棵红黑树，但是并未说明红黑树的节点到底是怎么产生的。

## 概述
![驱动层的关系调用图](http://static.oschina.net/uploads/img/201308/15213413_taa0.png)

在Binder驱动层，和 ioctl() 相对的动作是 binder_ioctl() 函数。在这个函数里，会先调用类似 copy_from_user() 这样的函数，来读取用户态的数据。然后，再调用 binder_thread_write() 和 binder_thread_read() 进行进一步的处理。


```c
if (bwr.write_size > 0) 
{
    ret = binder_thread_write(proc, thread, (void __user *)bwr.write_buffer,
                              bwr.write_size, &bwr.write_consumed);
    if (ret < 0) 
    {
        bwr.read_consumed = 0;
        if (copy_to_user(ubuf, &bwr, sizeof(bwr)))
            ret = -EFAULT;
        goto err;
    }
}
```

注意binder_thread_write()的前两个参数，一个是binder_proc指针，另一个是binder_thread指针，表示发起传输动作的进程和线程。binder_proc不必多说了，那个binder_thread是怎么回事？大家应该还记得前文提到的binder_proc里的4棵树吧，此处的binder_thread就是从threads树中查到的节点。


## 要进行跨进程调用，需要考虑什么？
1.  发起端：肯定包括发起端所从属的进程，以及实际执行传输动作的线程。当然，发起端的 BpBinder 更是重中之重。

2.  接收端：包括与发起端对应的 BBinder，以及目标进程、线程。

3.  待传输的数据：其实就是前文 IPCThreadState::writeTransactionData() 代码中的 binder_transaction_data了，需要注意的是，这份数据中除了包含简单数据，还可能包含其他 binder对象，这些对象或许对应 binder代理对象， 或许对应 binder 实体对象，视具体情况而定。

4.  如果我们的 IPC 动作需要接收应答（reply），该如何保证应答能准确无误地传回来？

5.  如何让系统中的多个传输动作有条不紊地进行。


## 传输机制的大体运作

Binder IPC机制 的大体思路是这样的，它将每次“传输并执行特定语义的”工作理解为一个小事务，既然所传输的数据是 binder_transaction_data 类型的，那么这种事务的类名可以相应地定为 binder_transaction。系统中当然会有很多事务啦，那么发向同一个进程或线程的若干事务就必须串行化起来，因此 binder驱动 为进程节点（binder_proc）和 线程节点（binder_thread） 都设计了个 todo队列。todo队列 的职责就是“串行化地组织待处理的事务”。


传输动作的基本目标就很明确了，就是想办法把发起端的一个 binder_transaction节点，插入到目标端 进程或其合适子线程的todo队列 去。

可是，该怎么找目标进程和目标线程呢？基本做法是先从 发起端的BpBinder 开始，找到与其对应的 binder_node节点，这个在前文阐述 binder_proc 的4棵红黑树时已经说过了，这里不再赘述。总之拿到 目标binder_node 之后，我们就可以通过其 proc域，拿到目标进程对应的 binder_proc了。如果偷懒的话，我们直接把binder_transaction节点插到这个binder_proc的todo链表去，就算完成传输动作了。当然，binder驱动做了一些更 精细 的调整。

binder驱动 希望能把 binder_transaction节点 尽量放到 目标进程 里的 某个线程 去，这样可以充分利用这个 进程中的binder工作线程。比如一个binder线程目前正睡着，它在等待其他某个线程做完某个事情后才会醒来，而那个工作又偏偏需要在当前这个 binder_transaction事务 处理结束后才能完成，那么我们就可以让那个睡着的线程先去做当前的 binder_transaction事务 ，这就达到充分利用线程的目的了。反正不管怎么说，如果 binder驱动 可以找到一个合适的线程，它就会把binder_transaction节点 插到它的 todo队列 去。而如果找不到合适的线程，还可以把节点插入目标 binder_proc的todo队列。

## 红黑树节点的产生过程

另一个要考虑的东西就是binder_proc里的那4棵树啦。前文在阐述binder_get_thread()时，已经看到过向threads树中添加节点的动作。那么其他3棵树的节点该如何添加呢？其实，秘密都在传输动作中。要知道，binder驱动在传输数据的时候，可不是仅仅简单地递送数据噢，它会分析被传输的数据，找出其中记录的binder对象，并生成相应的树节点。如果传输的是个binder实体对象，它不仅会在发起端对应的nodes树中添加一个binder_node节点，还会在目标端对应的refs_by_desc树、refs_by_node树中添加一个binder_ref节点，而且让binder_ref节点的node域指向binder_node节点。








