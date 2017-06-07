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



## Service Manager Service的主程序（C++层）
在代码中，一开始就打开了binder驱动，然后调用binder_become_context_manager() 让自己成为整个系统中唯一的上下文管理器，其实也就是service管理器啦。接着main()函数调用binder_loop()进入无限循环，不断监听并解析binder驱动发来的命令。

###　binder_open()

Service Manager Service必须先调用binder_open()来打开binder驱动，驱动文件为“/dev/binder”。

binder_open()的参数mapsize表示它希望**把binder驱动文件的多少字节映射到本地空间**。可以看到，Service Manager Service和普通进程所映射的binder大小并不相同。它把binder驱动文件的128K字节映射到内存空间，而普通进程则会映射binder文件里的BINDER_VM_SIZE（即1M减去8K）字节。
具体的映射动作由mmap()一句完成，该函数将binder驱动文件的一部分映射到进程空间。

```c
void* mmap ( void * addr , size_t len , int prot , int flags , int fd , off_t offset );
```
该函数会把“参数fd所指代的文件”中的一部分映射到进程空间去。这部分文件内容以offset为起始位置，以len为字节长度。其中，参数offset表明从文件起始处开始算起的偏移量。参数prot表明对这段映射空间的访问权限，可以是PROT_READ（可读）、PROT_WRITE （可写）、PROT_EXEC （可执行）、PROT_NONE（不可访问）。参数addr用于指出文件应被映射到进程空间的起始地址，一般指定为空指针，此时会由内核来决定起始地址。

binder_open()的返回值类型为binder_state*，里面记录着刚刚打开的binder驱动文件句柄以及mmap()映射到的最终目标地址。

以后，SMS会不断读取这段映射空间，并做出相应的动作。


### binder_become_context_manager()

binder_become_context_manager()的作用是让当前进程成为整个系统中唯一的上下文管理器，即service管理器。仅仅是把BINDER_SET_CONTEXT_MGR 发送到 binder驱动 而已。为整个系统的上下文管理器专门生成一个binder_node节点，并记入静态变量binder_context_mgr_node。


> 我们在这里多说两句，一般情况下，应用层的每个binder实体都会在binder驱动层对应一个binder_node节点，然而binder_context_mgr_node比较特殊，它没有对应的应用层binder实体。在整个系统里，它是如此特殊，以至于系统规定，任何应用都必须使用句柄0来跨进程地访问它。现在大家可以回想一下前文在获取SMS接口时说到的那句new BpBinder(0)，是不是能加深一点儿理解。


### binder_loop()
binder_loop()会先向binder驱动发出了BC_ENTER_LOOPER命令，接着进入一个for循环不断调用ioctl()读取发来的数据，接着解析这些数据。

#### BC_ENTER_LOOPER
binder_loop() 中发出 BC_ENTER_LOOPER 命令的目的，是为了告诉 binder驱动 “本线程要进入循环状态了”。在 binder驱动 中，凡是用到跨进程通信机制的线程，都会对应一个 binder_thread 节点。这里的 BC_ENTER_LOOPER 命令会导致这个节点的 looper 状态发生变化。


#### binder_parse()
binder_parse()负责解析从binder驱动读来的数据。
binder_parse()在合适的时机，会回调其func参数（binder_handler func）指代的回调函数，即前文说到的svcmgr_handler()函数。
inder_loop()就这样一直循环下去，完成了整个service manager service的工作。

# Service Manager Service解析收到的命令

```C
int binder_parse(struct binder_state *bs, struct binder_io *bio,
uint32_t *ptr, uint32_t size, binder_handler func)
```
之前利用ioctl()读取到的数据都记录在第三个参数ptr所指的缓冲区中，数据大小由size参数记录。其实这个buffer就是前文那个128字节的buffer。

从驱动层读取到的数据，实际上是若干BR命令。每个BR命令是由一个命令号(uint32)以及若干相关数据组成的，不同BR命令的长度可能并不一样。

每次ioctl()操作所读取的数据，可能会包含多个BR命令，所以binder_parse()需要用一个while循环来解析buffer中所有的BR命令。


## binder_txn信息
binder_txn说明了transaction到底在传输什么语义，而语义码就记录在其code域中。不同语义码需要携带的数据也是不同的，这些数据由data域指定。

简单地说，我们从驱动侧读来的binder_txn只是一种“传输控制信息”，它本身并不包含传输的具体内容，而只是指出具体内容位于何处。现在，工作的重心要转到如何解析传输的具体内容了，即binder_txn的data域所指向的那部分内容。


## svcmgr_handler()回调函数

###  如何解析add service
service manager进程里有一个全局性的svclist变量，记录着所有添加进系统的“service代理”信息，这些信息被组织成一条单向链表，即“服务向量表”。现在我们要看service manager是如何向这张表中添加新节点的。

```c
 case SVC_MGR_ADD_SERVICE:
        s = bio_get_string16(msg, &len);
        ptr = bio_get_ref(msg);
        allow_isolated = bio_get_uint32(msg) ? 1 : 0;
        if (do_add_service(bs, s, len, ptr, txn->sender_euid, allow_isolated))
            return -1;
        break;

```
假设某个服务进程调用Service Manager Service接口，向其注册service。这个注册动作到最后就会走到svcmgr_handler()的case SVC_MGR_ADD_SERVICE分支。

binder_txn所指的数据区域中应该包含一个字符串，一个binder对象以及一个uint32数据。binder_object，记录的就是新注册的service所对应的代理信息。此时binder_object的pointer域实际上已经不是指针值了，而是一个binder句柄值。



#### do_add_service
并不是随便找个进程就能向系统注册service噢。do_add_service()函数一开始先调用svc_can_register()，判断发起端是否可以注册service。如果不可以，do_add_service()就返回-1值。

#### svc_can_register
如果发起端是root进程或者system server进程的话，是可以注册service的，另外，那些在allowed[]数组中有明确记录的用户进程，也是可以注册service的，至于其他绝大部分普通进程，很抱歉，不允许注册service。在以后的软件开发中，我们有可能需要编写新的带service的用户进程（uid不为0或AID_SYSTEM），并且希望把service注册进系统，此时不要忘了修改allowed[]数组。


do_add_service()开始尝试在service链表里查询对应的service是否已经添加过了。如果可以查到，那么就不用生成新的service节点了。否则就需要在链表起始处再加一个新节点。节点类型为svcinfo。

### 如何解析get service
```c
case SVC_MGR_GET_SERVICE:
    case SVC_MGR_CHECK_SERVICE:
        s = bio_get_string16(msg, &len);
        ptr = do_find_service(bs, s, len, txn->sender_euid);
        if (!ptr)
            break;
        bio_put_ref(reply, ptr);
        return 0;
```
在service被注册进service manager之后，其他应用都可以调用ServiceManager的getService()来获取相应的服务代理，并调用代理的成员函数。这个getService()函数最终会向service manager进程发出SVC_MGR_GET_SERVICE命令。
一开始从msg中读取希望get的服务名，然后调用do_find_service()函数查询服务名对应的句柄值，最后把句柄值写入reply。


