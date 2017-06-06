# 概述

在Android中，系统提供的服务被包装成一个个系统级service，这些service往往会在设备启动之时添加进Android系统。在上一篇文档中，我们已经了解了BpBinder和BBinder的概念，而service实体的底层说到底就是一个BBinder实体。

如果某个程序希望享受系统提供的服务，它就必须调用系统提供的外部接口，向系统发出相应的请求。因此，Android中的程序必须先拿到和某个系统service对应的代理接口，然后才能通过这个接口，享受系统提供的服务。说白了就是我们得先拿到一个和目标service对应的合法BpBinder。

## 该怎么获取和系统service对应的代理接口呢？

Android是这样设计的：先启动一个特殊的系统服务，叫作Service Manager Service（简称SMS），它的基本任务就是管理其他系统服务。 **其他系统服务在系统启动之时，就会向SMS注册自己，于是SMS先记录下与那个service对应的名字和句柄值。** 有了句柄值就可以用来创建合法的BpBinder了。只不过在实际的代码中，SMS并没有用句柄值创建出BpBinder，这个其实没什么，反正指**代目标service实体的目的已经达到了**。后续当某程序需要享受某系统服务时，它必须先以“特定手法”获取SMS代理接口，并经由这个接口查询出**目标service对应的合法Binder句柄，然后再创建出合法的BpBinder对象。**

在此，我们有必要交代一下“Binder句柄”的作用。句柄说穿了是个简单的整数值，用来告诉Binder驱动我们想找的目标Binder实体是哪个。但是请注意，**句柄只对发起端进程和Binder驱动有意义，A进程的句柄直接拿到B进程，是没什么意义的。**也就是说，不同进程中指代相同Binder实体的句柄值可能是不同的。
在 A 进程中利用句柄1 创建的BpBinder对象 和 B 进程中利用句柄2 创建的 BpBinder对象可以指向同一个 Binder 实体。

SMS 记录了所有系统 service 所对应的 Binder句柄，它的核心功能就是维护好这些句柄值。后续，当用户进程需要获取某个系统 service 的代理时，SMS 就会在内部按 **service名** 查找到合适的句柄值，并“逻辑上”传递给用户进程，于是用户进程会得到一个新的合法句柄值，这个新句柄值可能在数值上和SMS所记录的句柄值**不同**，然而，它们指代的却是同一个Service实体。句柄的合法性是由Binder驱动保证的，这一点我们不必担心。

在Android里，对于Service Manager Service这个特殊的服务而言，其对应的代理端的句柄值已经**预先定死为0了**，所以我们直接new BpBinder(0)拿到的就是个合法的BpBinder，其对端为“Service Manager Service实体”（至少目前可以先这么理解）。那么对于其他“服务实体”对应的代理，句柄值又是多少呢？使用方又该如何得到这个句柄值呢？我们总不能随便蒙一个句柄值吧。正如我们前文所述，要得到某个服务对应的BpBinder，主要得借助Service Manager Service系统服务，查询出一个合法的Binder句柄，并进而创建出合法的BpBinder。

这里有必要澄清一下，利用SMS获取合法BpBinder的方法，并不是Android中得到BpBinder的唯一方法。另一种方法是，“起始端”经由一个已有的合法BpBinder，将某个binder实体或代理对象作为跨进程调用的参数，“传递”给“目标端”，这样目标端也可以拿到一个合法的BpBinder。

在跨进程通信方面，所谓的“传递”一般指的都是逻辑上的传递，所以应该打上引号。事实上，binder实体对象是不可能完全打包并传递到另一个进程的，而且也没有必要这么做。目前我们只需理解，binder架构会保证“传递”动作的目标端可以拿到一个和binder实体对象对应的代理对象即可。

SMS承担着让客户端获取合法BpBinder的责任。


# 具体使用Service Manager Service

要获取某系统service的代理接口，必须先得到IServiceManager代理接口。

在 C++ 层面调用的是 IServiceManager::asInterface(obj)，而这个obj参数就是new BpBinder(0)得到的对象。

Java层次，是这样获取IServiceManager接口的：ServiceManagerNative.asInterface(BinderInternal.getContextObject());

Java层次和C++层的代码在本质上是一致的。

-  ServiceManagerProxy
ServiceManagerProxy就是IServiceManager代理接口，用户要访问Service Manager Service服务，必须先拿到IServiceManager 代理接口，而 ServiceManagerProxy 就是代理接口的实现。

```java
private IBinder mRemote;
public ServiceManagerProxy(IBinder remote) 
{
    mRemote = remote;
}
```

其实说白了，mRemote的核心包装的就是句柄为0的BpBinder对象.日后，当我们通过IServiceManager代理接口访问SMS时，其实调用的就是ServiceManagerProxy的成员函数。比如getService()、checkService()等等。


- ServiceManagerNative

它继承了Binder，实现了IServiceManager，然而却是个虚有其表的class。它唯一有用的大概就是前文列出的那个静态成员函数asInterface()了，而其他成员函数（像onTransact()）就基本上没什么用。

这里透出一个信息，既然Java层的ServiceManagerNative没什么大用处，是不是表示C++层也缺少对应的SMS服务实体呢？在后文我们可以看到，的确是这样的，Service Manager Service在C++层被实现成一个独立的进程，而不是常见的Binder实体。


### 通过addService()来注册系统服务

```java
power = new PowerManagerService();
    ServiceManager.addService(Context.POWER_SERVICE, power);
```
addService()的第一个参数就是所注册service的名字，比如上面的POWER_SERVICE对应的字符串就是"power"。第二个参数传入的是service Binder实体。Service实体在Service Manager Service一侧会被记录成相应的句柄值.




### 通过getService()来获取某系统服务的代理接口
ServiceManagerProxy中的getService()等成员函数，仅仅是把语义整理进parcel，并通过mRemote将parcel传递到目标端而已。


# Service Manager Service的运作机制

## Service Manager Service服务的启动
前文说ServiceManagerNative虚有其表，而且没有子类，那么Service Manager Service服务的真正实现代码位于何处呢？答案就在init.rc脚本里。


## Service Manager Service是如何管理service句柄的
在C语言层次，简单地说并不存在一个单独的ServiceManager结构。整个service管理机制都被放在一个独立的进程里了，该进程对应的实现文件就是Service_manager.c。

进程里有一个全局性的svclist变量：它记录着所有添加进系统的“service代理”信息，这些信息被组织成一条单向链表，我们不妨称这条链表为“服务向量表”。


日后，当应用调用getService()获取系统服务的代理接口时，SMS就会搜索这张“服务向量表”，查找是否有节点能和用户传来的服务名匹配，如果能查到，就返回对应的sp<IBinder>，这个接口在远端对应的实体就是“目标Service实体”。
