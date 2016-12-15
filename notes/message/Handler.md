进程：每个app运行时前首先创建一个进程，该进程是由Zygote fork出来的，用于承载App上运行的各种Activity/Service等组件。 大多数情况一个App就运行在一个进程中，除非在AndroidManifest.xml中配置Android:process属性，或通过native代码fork进程.

线程：线程对应用来说非常常见，比如每次new Thread().start都会创建一个新的线程。该线程与App所在进程之间资源共享，从Linux角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体，在CPU看来进程或线程无非就是一段可执行的代码，CPU采用CFS调度算法，保证每个task都尽可能公平的享有CPU时间片。


对于线程既然是一段可执行的代码，当可执行代码执行完成后，线程生命周期便该终止了，线程退出。而对于主线程，我们是绝不希望会被运行一段时间，自己就退出，那么如何保证能一直存活呢？简单做法就是可执行代码是能一直执行下去的，死循环便能保证不会被退出，例如，binder线程也是采用死循环的方法，通过循环方式不同与Binder驱动进行读写操作，当然并非简单地死循环，无消息时会休眠。但这里可能又引发了另一个问题，既然是死循环又如何去处理其他事务呢？通过创建新线程的方式。

代码ActivityThread.main()：

```java
public static void main(String[] args) {
.... //创建Looper和MessageQueue对象，用于处理主线程的消息
Looper.prepareMainLooper();
//创建ActivityThread对象
ActivityThread thread = new ActivityThread();
//建立Binder通道 (创建新线程)
thread.attach(false);
Looper.loop();
//消息循环运行
throw new RuntimeException("Main thread loop unexpectedly exited");
}
```

ActivityThread实际上并非线程，不像HandlerThread类，ActivityThread并没有真正继承Thread类，只是往往运行在主线程，该人以线程的感觉，其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。
主线程的死循环一直运行是不是特别消耗CPU资源呢？ 其实不然，这里就涉及到Linux pipe/epoll机制，简单说就是在主线程的MessageQueue没有消息时，便阻塞在loop的queue.next()中的nativePollOnce()方法里，详情见Android消息机制1-Handler(Java层)，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源。


(3) Activity的生命周期是怎么实现在死循环体外能够执行起来的？

ActivityThread的内部类H继承于Handler，通过handler消息机制，简单说Handler机制用于同一个进程的线程间通信。

Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施：
在H.handleMessage(msg)方法中，根据接收到不同的msg，执行相应的生命周期。


system_server进程是系统进程，java framework框架的核心载体，里面运行了大量的系统服务，比如这里提供ApplicationThreadProxy（简称ATP），ActivityManagerService（简称AMS），这个两个服务都运行在system_server进程的不同线程中，由于ATP和AMS都是基于IBinder接口，都是binder线程，binder线程的创建与销毁都是由binder驱动来决定的。

App进程则是我们常说的应用程序，主线程主要负责Activity/Service等组件的生命周期以及UI相关操作都运行在这个线程； 另外，每个App进程中至少会有两个binder线程 ApplicationThread(简称AT)和ActivityManagerProxy（简称AMP），除了图中画的线程，其中还有很多线程，比如signal catcher线程等，这里就不一一列举。

Binder用于不同进程之间通信，由一个进程的Binder客户端向另一个进程的服务端发送事务，比如图中线程2向线程4发送事务；而handler用于同一个进程中不同线程的通信，比如图中线程4向主线程发送消息。

结合图说说Activity生命周期，比如暂停Activity，流程如下：

	1. 线程1的AMS中调用线程2的ATP；（由于同一个进程的线程间资源共享，可以相互直接调用，但需要注意多线程并发问题）

	2. 线程2通过binder传输到App进程的线程4；

	3. 线程4通过handler消息机制，将暂停Activity的消息发送给主线程；

	4. 主线程在looper.loop()中循环遍历消息，当收到暂停Activity的消息时，便将消息分发给ActivityThread.H.handleMessage()方法，再经过方法的调用，最后便会调用到Activity.onPause()，当onPause()处理完后，继续循环loop下去。



Looper:

对于looper的典型例子:
```java
Looper.prepare();
mHandler = new Handler() {
     public void handleMessage(Message msg) {
          // handle message
     }
};
Looper.loop();
```
对于 prepare 方法，每个线程只能执行一次，当检测到当前的线程已经执行过这个方法，则会抛出异常。
```java
static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
// ..............

private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
```
对于 ThreadLocal 类，这个称为线程本地存储，以线程为单位，实现资源的共享。每个线程有自己的私有区域，线程间是不能互相访问的。  有的地方也能看到用 ThreadLocal 实现线程内的单例。

其中对应的set 和 get 方法，这样就能实现一个线程只有一个 looper ，检查的时候只要判断当前的线程本地存储是否有 looper 就能确定当前的线程是否执行过 prepare 。
```java
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}

public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null)
            return (T)e.value;
    }
    return setInitialValue();
}
```
Looper的构造方法中需要传递一个boolean值的参数，表示创建的这个looper是否可以被取消，直观上说就是是否可以调用quit方法。
```java
private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```
对于默认的looper构造传的是true。
```java
/** Initialize the current thread as a looper.
  * This gives you a chance to create handlers that then reference
  * this looper, before actually starting the loop. Be sure to call
  * {@link #loop()} after calling this method, and end it by calling
  * {@link #quit()}.
  */
public static void prepare() {
    prepare(true);
}
```


有个looper之后，就可以创建熟知的 handler 对象了。handler 在创建的时候，都是会检查当前的线程是否有 looper ，如果没有，则会抛出异常，不能正常的创建，毕竟这是一个消息机制必不可少的元素。
```java
public Handler(Callback callback, boolean async) {
    if (FIND_POTENTIAL_LEAKS) {
        final Class<? extends Handler> klass = getClass();
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
            Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                klass.getCanonicalName());
        }
    }

    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
            "Can't create handler inside thread that has not called Looper.prepare()");
    }
    mQueue = mLooper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
```

接下来就可以调用 loop 方法进入循环模式。
```java
public static void loop() {
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    final MessageQueue queue = me.mQueue;

    // Make sure the identity of this thread is that of the local process,
    // and keep track of what that identity token actually is.
    Binder.clearCallingIdentity();
    final long ident = Binder.clearCallingIdentity();

    for (;;) {
        Message msg = queue.next(); // might block
        if (msg == null) {
            // No message indicates that the message queue is quitting.
            return;
        }

        // This must be in a local variable, in case a UI event sets the logger
        final Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " +
                    msg.callback + ": " + msg.what);
        }

        final long traceTag = me.mTraceTag;
        if (traceTag != 0) {
            Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
        }
        try {
            msg.target.dispatchMessage(msg);
        } finally {
            if (traceTag != 0) {
                Trace.traceEnd(traceTag);
            }
        }

        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }

        // Make sure that during the course of dispatching the
        // identity of the thread wasn't corrupted.
        final long newIdent = Binder.clearCallingIdentity();
        if (ident != newIdent) {
            Log.wtf(TAG, "Thread identity changed from 0x"
                    + Long.toHexString(ident) + " to 0x"
                    + Long.toHexString(newIdent) + " while dispatching to "
                    + msg.target.getClass().getName() + " "
                    + msg.callback + " what=" + msg.what);
        }

        msg.recycleUnchecked();
    }
}
```
直观上我们就可以看到这是一个死循环，不断的通过 queue.next() 取出消息，而后通过 msg.target.dispatchMessage(msg); 去分发，处理消息。最后会通过 msg.recycleUnchecked(); 回收消息以备循环使用。
上面的消息处理是一个死循环，我们知道在主线程也有个 looper 这样不停的死循环执行，怎么就不会卡死呢？CPU 怎么不会爆表呢。
注意 Message msg = queue.next(); // might block 这里的注释，说可能产生 block （阻塞）。这个涉及到 MessageQueue，native 层 和 epoll 机制，当队列中没有消息的时候，这个时候，就会被阻塞挂起，这时候CPU就会被释放，去做其他任务，这样就不会浪费CPU资源。


MessageQueue:

对于期初构造 looper 的时候传递了一个参数，表示了是否可以退出，在构造 looper 的时候 ，这个参数，也会传递到 MessageQueue  的构造方法中。
looper 中的quit 方法最终调用的是 MessageQueue 中的 quit 方法，在 quit 方法中，假如当初传递的参数是 false，则会抛出异常，即表示不能退出的含义。

```java
MessageQueue(boolean quitAllowed) {
    mQuitAllowed = quitAllowed;
    mPtr = nativeInit();
}

void quit(boolean safe) {
    if (!mQuitAllowed) {
        throw new IllegalStateException("Main thread not allowed to quit.");
    }

    synchronized (this) {
        if (mQuitting) {
            return;
        }
        mQuitting = true;

        if (safe) {
            removeAllFutureMessagesLocked();
        } else {
            removeAllMessagesLocked();
        }

        // We can assume mPtr != 0 because mQuitting was previously false.
        nativeWake(mPtr);
    }
}
```

在quit 方法中也区分了 safe 和 unsafe。安全的方式只移除那些还未开始的消息，非安全的方式是移除所有的消息。


再细看 MessageQueue 的构造方法中有个 nativeInit();说明这里有涉及 native 方法。在整个消息机制中 只有 MessageQueue 是涉及 Java 层和 Native 层的。

```java
Message next() {
    // Return here if the message loop has already quit and been disposed.
    // This can happen if the application tries to restart a looper after quit
    // which is not supported.
    final long ptr = mPtr;
    if (ptr == 0) {
        return null;
    }

    int pendingIdleHandlerCount = -1; // -1 only during first iteration
    int nextPollTimeoutMillis = 0;
    for (;;) {
        if (nextPollTimeoutMillis != 0) {
            Binder.flushPendingCommands();
        }

        nativePollOnce(ptr, nextPollTimeoutMillis);

        synchronized (this) {
            // Try to retrieve the next message.  Return if found.
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages;
            if (msg != null && msg.target == null) {
                // Stalled by a barrier.  Find the next asynchronous message in the queue.
                do {
                    prevMsg = msg;
                    msg = msg.next;
                } while (msg != null && !msg.isAsynchronous());
            }
            if (msg != null) {
                if (now < msg.when) {
                    // Next message is not ready.  Set a timeout to wake up when it is ready.
                    nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                } else {
                    // Got a message.
                    mBlocked = false;
                    if (prevMsg != null) {
                        prevMsg.next = msg.next;
                    } else {
                        mMessages = msg.next;
                    }
                    msg.next = null;
                    if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                    msg.markInUse();
                    return msg;
                }
            } else {
                // No more messages.
                nextPollTimeoutMillis = -1;
            }

            // Process the quit message now that all pending messages have been handled.
            if (mQuitting) {
                dispose();
                return null;
            }

            // If first time idle, then get the number of idlers to run.
            // Idle handles only run if the queue is empty or if the first message
            // in the queue (possibly a barrier) is due to be handled in the future.
            if (pendingIdleHandlerCount < 0
                    && (mMessages == null || now < mMessages.when)) {
                pendingIdleHandlerCount = mIdleHandlers.size();
            }
            if (pendingIdleHandlerCount <= 0) {
                // No idle handlers to run.  Loop and wait some more.
                mBlocked = true;
                continue;
            }

            if (mPendingIdleHandlers == null) {
                mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
            }
            mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
        }

        // Run the idle handlers.
        // We only ever reach this code block during the first iteration.
        for (int i = 0; i < pendingIdleHandlerCount; i++) {
            final IdleHandler idler = mPendingIdleHandlers[i];
            mPendingIdleHandlers[i] = null; // release the reference to the handler

            boolean keep = false;
            try {
                keep = idler.queueIdle();
            } catch (Throwable t) {
                Log.wtf(TAG, "IdleHandler threw exception", t);
            }

            if (!keep) {
                synchronized (this) {
                    mIdleHandlers.remove(idler);
                }
            }
        }

        // Reset the idle handler count to 0 so we do not run them again.
        pendingIdleHandlerCount = 0;

        // While calling an idle handler, a new message could have been delivered
        // so go back and look again for a pending message without waiting.
        nextPollTimeoutMillis = 0;
    }
}
```

这里稍微记录下这个方法，怎么看这个方法可能都是 消息机制在 Java 层相对难以理解的一个。


	1. MessageQueue 通过 next 方法从消息队列中取出 消息执行，nativePollOnce 可以看出这里又牵扯到 native 方法 ，nativePollOnce  是一个阻塞操作，为什么会这样呢？因为在 native 层也有消息机制，从这里能看出 Android 系统中，native 层的消息优先级比 Java 层的高。注意: native 层的消息机制和 Java 层的没有任何关系。顺带说下参数 mPtr 是在构造参数里调用 native方法初始化的，这个参数是 NativeMessageQueue 的指针。nextPollTimeoutMillis 代表了超时的时间，这个是用来描述当消息队列中没有新的消息需要处理的时候，当前线程需要进入睡眠等待状态的时间。

		* 这个可以取值 -1 ，代表了队列中没有消息需要处理，需要一直休眠阻塞下去，直到被其他线程唤醒为止。
		* 取值为0，表示当前的线程不需要进入等待状态，即使当前的消息队列中没有新的消息需要处理。
		* 取值为大于0，表示当消息队列中没有新的消息的时候，等待大于0的时间就返回。

	2. mMessages 表示当前处理的消息， 通过循环取出有 Handler 并且是异步的消息然后返回。如果没有找到就将  nextPollTimeoutMillis 置为 -1 表示队列中没有消息，需要一直休眠了。
	3. mIdleHandlers 表示的是空闲处理器，当消息队列中没有需要处理的消息的时候，做的一些工作，例如垃圾回收等，完成这部分操作之后就会重置pendingIdleHandlerCount 的值为0，保证整个循环中只执行一次，这个值为0 还对 nextPollTimeoutMillis 有影响，在上半部分有个 nextPollTimeoutMillis  的判断。同时 nextPollTimeoutMillis 也被置为 0 ，表示不需要进入等待状态，立即检查消息队列。


在这里，nextPollTimeoutMillis 置为 0 可能会造成一定的干扰，理解立即检查不难，似乎每次都会被置为 0 ，那么 -1 是不是就没有意义了呢？注意另外一个值  pendingIdleHandlerCount 也被置为了0 ，注意取消息的时候关于这个 pendingIdleHandlerCount 的检查，当为 0 的时候，是直接 continue了。

看下往队列中添加消息的方法。

```java
boolean enqueueMessage(Message msg, long when) {
    if (msg.target == null) {
        throw new IllegalArgumentException("Message must have a target.");
    }
    if (msg.isInUse()) {
        throw new IllegalStateException(msg + " This message is already in use.");
    }

    synchronized (this) {
        if (mQuitting) {
            IllegalStateException e = new IllegalStateException(
                    msg.target + " sending message to a Handler on a dead thread");
            Log.w(TAG, e.getMessage(), e);
            msg.recycle();
            return false;
        }

        msg.markInUse();
        msg.when = when;
        Message p = mMessages;
        boolean needWake;
        if (p == null || when == 0 || when < p.when) {
            // New head, wake up the event queue if blocked.
            msg.next = p;
            mMessages = msg;
            needWake = mBlocked;
        } else {
            // Inserted within the middle of the queue.  Usually we don't have to wake
            // up the event queue unless there is a barrier at the head of the queue
            // and the message is the earliest asynchronous message in the queue.
            needWake = mBlocked && p.target == null && msg.isAsynchronous();
            Message prev;
            for (;;) {
                prev = p;
                p = p.next;
                if (p == null || when < p.when) {
                    break;
                }
                if (needWake && p.isAsynchronous()) {
                    needWake = false;
                }
            }
            msg.next = p; // invariant: p == prev.next
            prev.next = msg;
        }

        // We can assume mPtr != 0 because mQuitting is false.
        if (needWake) {
            nativeWake(mPtr);
        }
    }
    return true;
}
```

这个方法的目的是找到 当前加入的这个消息在消息队列中合适的位置，是立即执行，亦或者插入到消息队列中。
其中会根据当前的消息循环 next 是否被阻塞，决定是否执行唤醒操作。

Handler:

看下 Handler 的构造函数
```java
public Handler(Callback callback, boolean async) {
    if (FIND_POTENTIAL_LEAKS) {
        final Class<? extends Handler> klass = getClass();
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
            Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                klass.getCanonicalName());
        }
    }

    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
            "Can't create handler inside thread that has not called Looper.prepare()");
    }
    mQueue = mLooper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}

public Handler(Looper looper, Callback callback, boolean async) {
    mLooper = looper;
    mQueue = looper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
```

主要是这两种构造函数，第一种可以可以指定回调函数和消息的处理方式(是否是异步处理)。第二种可以指定 Looper ，回调函数 和消息的处理方式。

上面说到 Looper 的 loop 方法在循环取出消息和处理的时候提到一个  msg.target.dispatchMessage(msg); 分发处理。平时的使用经验让我们知道这个 target 其实是个 Handler 。
所以接下来看下这个 dispatchMessage 分发消息的方法。

```java
public void dispatchMessage(Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
}
```
从代码中可以，假如 Message 有回调的情况下， 优先执行的是 Message 的回调方法。其次，如果没有回调的情况下，检查 Handler 构造的时候是否有设置回调，如果有优先调用这个回调。再次，才会去调用子类覆写的 handleMessage 方法。我们平时使用的时候，常常就是使用的这个再次的方式。

上面有说过消息队列是负责消息的排队的，接下来看下到底是怎么产生消息，怎么进入队列的。
首先看下几个我们常用的产生消息的方式。

```java
public final Message obtainMessage()
{
    return Message.obtain(this);
}
public final boolean sendMessage(Message msg)
{
    return sendMessageDelayed(msg, 0);
}

public final boolean sendEmptyMessage(int what)
{
    return sendEmptyMessageDelayed(what, 0);
}

public final boolean post(Runnable r)
{
  return  sendMessageDelayed(getPostMessage(r), 0);
}
```
平时常用的就这四种种方式产生消息并且发送消息，对于 Message 的 sendToTarget 方法，本质上还是调用的 sendMessage 这个后面再说，这个几个方法最后都会走 sendMessageAtTime 方法。
```java
public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    MessageQueue queue = mQueue;
    if (queue == null) {
        RuntimeException e = new RuntimeException(
                this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return false;
    }
    return enqueueMessage(queue, msg, uptimeMillis);
}

private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
    msg.target = this;
    if (mAsynchronous) {
        msg.setAsynchronous(true);
    }
    return queue.enqueueMessage(msg, uptimeMillis);
}
```
在  sendMessageAtTime 方法中进行了一些检查，检查是否已经有  MessageQueue ，如果没有则抛出异常， 毕竟 MessageQueue 也是消息机制不可或缺的元素。
检查通过之后就会进入  enqueueMessage  方法， 这个方法中会调用 MessageQueue  的 enqueueMessage  方法将 消息添加入队列中(这个方法后面详细再说)，同时会根据构造 Handler 的时候设置的消息处理方式来为 message 设置相应的属性。


从分析中可以看出，Handler 本身没有太多实质性的操作，都是借助于 Meesge ，MessageQueue ，Looper 这个几个类。说明 Handler 只是一个很强的辅助类而已，方便开发者 产生消息->发送消息->处理消息。


其他:
IdleHandler:
空闲时处理器，这个只有在 looper 执行消息循环的第一次会执行。

Message：

作为消息的封装类，是消息的载体。
```java
public Message() {
}


public int what;
public int arg1;
public int arg2;
public Object obj;
public Messenger replyTo;
long when;
Handler target;
Runnable callback;
private static Message sPool;
private static final int MAX_POOL_SIZE = 50;
```
以上是 Message 的构造方法和比较重要的属性。
静态变量sPool的数据类型为Message，通过next成员变量，维护一个消息池；静态变量MAX_POOL_SIZE代表消息池的可用大小；消息池的默认大小为50。

看下几个比较重要的方法
```java
public static Message obtain() {
    synchronized (sPoolSync) {
        if (sPool != null) {
            Message m = sPool;
            sPool = m.next;
            m.next = null;
            m.flags = 0; // clear in-use flag
            sPoolSize--;
            return m;
        }
    }
    return new Message();
}
```
还有其他几个带参 的 obtain 方法，但是都需要调用这个无参的，可以看到 从静态变量 sPool  中取出一个 Message 即返回，若是sPool 为空，则新建一个 Message 返回。

```java
public void recycle() {
    if (isInUse()) {
        if (gCheckRecycle) {
            throw new IllegalStateException("This message cannot be recycled because it "
                    + "is still in use.");
        }
        return;
    }
    recycleUnchecked();
}
void recycleUnchecked() {
    // Mark the message as in use while it remains in the recycled object pool.
    // Clear out all other details.
    flags = FLAG_IN_USE;
    what = 0;
    arg1 = 0;
    arg2 = 0;
    obj = null;
    replyTo = null;
    sendingUid = -1;
    when = 0;
    target = null;
    callback = null;
    data = null;

    synchronized (sPoolSync) {
        if (sPoolSize < MAX_POOL_SIZE) {
            next = sPool;
            sPool = this;
            sPoolSize++;
        }
    }
}
```
当调用回收方法的时候，就会将当前的消息插入到静态变量 sPool 的头部，实现循环利用。
