# 什么是 Binder

简单地说，Binder 是 Android 平台上的一种跨进程交互技术。

Binder 机制的两层含义：
- 是一种跨进程通信手段（IPC，Inter-Process Communication）
- 是一种远程过程调用手段（RPC，Remote Procedure Call）

从实现的角度来说，Binder 核心被实现成一个 Linux 驱动程序，并运行于内核态。这样它才能具有强大的跨进程访问能力。

## 简述
Binder 代理方大概对应于 C++ 层次的 BpBinder 对象，而 Binder 响应方则对应于 BBinder 对象。

Binder 代理方主要只负责了“传递信息”的工作，并没有起到“远程过程调用”的作用，如果要支持远程过程调用，我们还必须提供“接口代理方”和“接口实现体”。

A进程并不直接和 BpBinder（Binder代理）打交道，而是通过调用 BpInterface （接口代理）的成员函数来完成远程调用的。此时，BpBinder 已经被聚合进 BpInterface 了，它在 BpInterface 内部完成了一切跨进程的机制。另一方面，与 BpInterface 相对的响应端实体就是 BnInterface（接口实现）了。需要注意的是，BnInterface 是继承于 BBinder的，它并没有采用聚合的方式来包含一个BBinder 对象。

对于远程调用的客户端而言，主要搞的就是两个东西，一个是“Binder代理”，一个是“接口代理”。而服务端主要搞的则是“接口实现体”。因为binder是一种跨进程通信机制，所以还需要一个专门的管理器来为通信两端牵线搭桥，这个管理器就是Service Manager Service。


# Binder 相关接口和类
Android的整个跨进程通信机制都是基于Binder的，这种机制不但会在底层使用，也会在上层使用，所以必须提供Java和C++两个层次的支持。


## Java 层的 Binder

Android要求所有的Binder实体都必须实现IBinder接口。

- IBinder
所有的 Binder 实体都必须实现 IBinder 接口。
- IInterface
不管代理方还是实现方，都必须实现 IInterface 接口。


## C++ 层的 Binder

- BpBinder
```C+
    BpBinder(int32_t handle);
    inline  int32_t     handle() const { return mHandle; }

    virtual const String16&    getInterfaceDescriptor() const;
    virtual bool        isBinderAlive() const;
    virtual status_t    pingBinder();
    virtual status_t    dump(int fd, const Vector<String16>& args);

    virtual status_t    transact(uint32_t code, const Parcel& data,
                                    Parcel* reply, uint32_t flags = 0);
    virtual status_t    linkToDeath(const sp<DeathRecipient>& recipient,
                                    void* cookie = NULL, uint32_t flags = 0);
    virtual status_t    unlinkToDeath(const wp<DeathRecipient>& recipient,
                                            void* cookie = NULL, uint32_t flags = 0,
                                            wp<DeathRecipient>* outRecipient = NULL);
```
为代理端的核心，BpBinder 最重要的职责就是实现跨进程传输的传输机制，至于具体传输的是什么语义，它并不关心。我们观察它的 transact() 函数的参数，可以看到所有的语义都被打包成 Parcel 了。


- BpInterface

```C++
template<typename INTERFACE>
class BpInterface : public INTERFACE, public BpRefBase
{
public:
BpInterface(const sp<IBinder>& remote);

protected:
    virtual IBinder*     onAsBinder();
};
////////// 基类BpRefBase的定义
class BpRefBase : public virtual RefBase
{
protected:
                            BpRefBase(const sp<IBinder>& o);
    virtual                 ~BpRefBase();
    virtual void            onFirstRef();
    virtual void            onLastStrongRef(const void* id);
    virtual bool            onIncStrongAttempted(uint32_t flags, const void* id);
    inline  IBinder*        remote()                { return mRemote; }
    inline  IBinder*        remote() const          { return mRemote; }

private:
BpRefBase(const BpRefBase& o);
    BpRefBase&              operator=(const BpRefBase& o);
    IBinder* const           mRemote;
    RefBase::weakref_type*  mRefs;
    volatile int32_t         mState;
};
```

BpInterface使用了模板技术，而且因为它继承了BpRefBase，所以先天上就聚合了一个mRemote成员，这个成员记录的就是前面所说的BpBinder对象啦。以后，我们还需要继承BpInterface<>实现我们自己的代理类。
 在实际的代码中，我们完全可以创建多个聚合同一BpBinder对象的代理对象，这些代理对象就本质而言，对应着同一个远端binder实体。在Android框架中，常常把指向同一binder实体的多个代理称为token，这样即便这些代理分别处于不同的进程中，它们也具有了某种内在联系。


- BBinder

 Binder 远程通信的目标端实体必须继承于BBinder类，该类和BpBinder相对，主要关心的只是传输方面的东西，不太关心所传输的语义。

 有两个关键的方法，transact()和onTransact()。transact()内部会调用onTransact()，从而走到用户所定义的子类的onTransact()里。这个onTransact()的一大作用就是解析经由Binder机制传过来的语义了。

- BnInterface
远程通信目标端的另一个重要类是BnInterface<>，它是与BpInterface<>相对应的模板类，比较关心传输的语义。一般情况下，服务端并不直接使用BnInterface<>，而是使用它的某个子类。为此，我们需要编写一个新的BnXXX子类，并重载它的onTransact()成员函数。


## 几个重要的C++宏或模板

- DECLARE_META_INTERFACE()
这里有最关键的就是asInterface()函数了，这个函数将承担把BpBinder打包成BpInterface的职责。

-  IMPLEMENT_META_INTERFACE()
 与DECLARE_META_INTERFACE相对的就是IMPLEMENT_META_INTERFACE宏。实现了关键的asInterface()函数。

请注意，asInterface()函数中会先尝试调用queryLocalInterface()来获取intr。此时，如果asInterface()的obj参数是个代理对象（BpBinder），那么intr = static_cast<ICamera*>(obj->queryLocalInterface(...)一句得到的intr基本上就是NULL啦。这是因为除非用户编写的代理类重载queryLocalInterface()函数，否则只会以默认函数为准。

另一方面，如果obj参数是个实现体对象（BnInterface对象）的话，那么queryLocalInterface()函数的默认返回值就是实体对象的this指针了

- interface_cast

 不过，我们经常使用的其实并不是asInterface()函数，而是interface_cast()，它简单包装了asInterface()：

以上就是关于C++层次中一些binder元素的介绍。


# ProcessState
在每个进程中，会有一个全局的ProcessState对象。这个很容易理解，ProcessState的字面意思不就是“进程状态”吗，当然应该是每个进程一个ProcessState。

Binder内核被设计成一个驱动程序，所以ProcessState里专门搞了个mDriverFD域，来记录binder驱动对应的句柄值，以便随时和binder驱动通信。ProcessState对象采用了典型的单例模式，在一个应用进程中，只会有唯一的一个ProcessState对象，它将被进程中的多个线程共用，因此每个进程里的线程其实是共用所打开的那个驱动句柄（mDriverFD）的。

另一个比较有意思的域是mHandleToObject。它是本进程中记录所有BpBinder的向量表噢，非常重要。我们前文已经说过，BpBinder是代理端的核心，现在终于看到它的藏身之处了。在Binder架构中，应用进程是通过“binder句柄”来找到对应的BpBinder的。从这张向量表中我们可以看到，那个句柄值其实对应着这个向量表的下标。