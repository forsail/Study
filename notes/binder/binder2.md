#  摘要
Android 系统是基于 Linux  内核开发的。Binder 是 Android 系统中的进程间通信方式。

## 目前已有的 IPC(进程间通信) 方式。
- 管道
- system V IPC 
- socket


# 引言
CS（client-Server）模式广泛应用于各个领域，为了向应用开发者提供丰富多样的功能，这种通信方式更是无处不在，诸如媒体播放，视音频频捕获，到各种让手机更智能的传感器（加速度，方位，温度，光亮度等）都由不同的 Server 负责管理，应用程序只需做为 Client 与这些 Server 建立连接便可以使用这些服务，花很少的时间和精力就能开发出令人眩目的功能。

## 为什么采用 binder

1. 对于复杂性和可靠性而言。CS 广泛采用，对于 IPC 是个挑战，目前 Linux 支持的 IPC 中，只有 Socket 是支持 CS 的通信方式的，当然可以在已有的这些底层机制之上架设一套协议来实现 CS 通信，但是这样增加了系统的复杂性，可靠性也难以得到保证。

2. 对于传输的性能而言。Socket 作为一款通用的接口，传输效率低，开销大，主要用于跨网络的进程间通信和本机上进程间的低速通信。消息队列和管道采用存储-转发方式，即数据先发送方缓存区拷贝到内核开辟的缓存区中，然后再从内核缓存区拷贝到接收方缓存区，至少两次拷贝过程。共享内存虽然无需拷贝，但是控制复杂，难以使用。

共享内存->不想要内存拷贝。
Binder 有1次内存拷贝。
Socket/管道/消息队列， 有两次内存拷贝。

3. 从安全性考虑。Android 是个开放式的平台，应用来源广泛，确保智能终端的安全则是非常重要的。传统的 IPC 是无法获得对方进程可靠的 UID/PID (用户ID/进程ID)，从而无法鉴别对方的身份。 Android 为每个安装好的应用程序分配了自己的 UID，所以进程的 UID  是鉴别进程身份的重要标识。传统的 IPC 只能由用户阻塞数据包里填入 UID/PID，这样是不可靠的，容易被篡改。可靠的身份标记只能 IPC 机制本身在内核中添加。传统的 IPC 访问点都是开放的，无法建立私有的通道。比如命名管道的名称， system V 的键值， Socket 的 IP 地址或者文件名都是开放的，主要知道这些接入点的程序都可以和对端建立链接，不管怎样都无法阻止恶意程序通过猜测对方地址获得连接。

Binder 是基于 CS 通信模式的，传输过程中只需要一次拷贝，为发送方添加 UID/PID 身份，既支持实名 Binder 也支持匿名 Binder ，安全性高。


# Binder 的通信方式

Binder 使用 Client-Server 通信方式：1. 一个进程作为 Server 提供诸如视频/音频解码，视频捕获，地址本查询，网络连接等服务；2. 多个进程作为 Client 向 Server 发起服务请求，获得所需要的服务。
要想实现 Client-Server 通信据必须实现以下两点：
- 一是 Server 必须有确定的**访问接入点**或者说**地址**来接受 Client 的请求，并且 Client 可以通过某种途径获知 Server 的地址；
- 二是制定 Command-Reply 协议来传输数据。例如在网络通信中 Server 的访问接入点就是 Server 主机的IP地址+端口号，传输协议为 TCP 协议。对 Binder 而言，Binder 可以看成 Server 提供的实现某个特定服务的**访问接入点**， Client 通过这个*‘地址’*向 Server 发送请求来使用该服务；对 Client 而言，Binder 可以看成是通向 Server 的管道入口，要想和某个 Server 通信首先必须建立这个管道并获得管道入口。

# 面向对象的 Binder IPC

与其它 IPC 不同，Binder 使用了**面向对象**的思想来描述作为**访问接入点的 Binder**及其在 Client 中的**入口**：Binder 是一个实体位于 Server 中的对象，该对象提供了一套方法用以实现**对服务的请求**，就象类的成员函数。遍布于 Client 中的入口可以看成指向这个 Binder 对象的‘指针’，一旦获得了这个‘指针’就可以调用该对象的方法访问 Server 。在 Client 看来，通过 Binder ‘指针’调用其提供的方法和调用其它任何本地对象的方法并无区别，尽管 Binder 的实体位于远端 Server 中，而后者实体位于本地内存中。 ‘指针’是 C++ 的术语，而更通常的说法是引用，即 Client 通过 Binder 的引用访问 Server。而软件领域另一个术语‘句柄’也可以用来表述 Binder 在 Client 中的存在方式。从通信的角度看， Client 中的 Binder 也可以看作是 Server Binder 的‘代理’，在本地**代表远端 Server** 为 Client 提供服务。本文中会使用‘引用’或‘句柄’这个两广泛使用的术语。

面向对象思想的引入将进程间通信转化为通过对某个 Binder 对象的引用调用该对象的方法，而其独特之处在于 Binder 对象是一个可以**跨进程引用**的对象，**它的实体位于一个进程中，而它的引用却遍布于系统的各个进程之中。** 最诱人的是，这个引用和Java里引用一样既可以是强类型，也可以是弱类型，而且可以从一个进程传给其它进程，让大家都能访问同一 Server，就象将一个对象或引用赋值给另一个引用一样。Binder **模糊了进程边界**，淡化了进程间通信过程，整个系统仿佛运行于同一个面向对象的程序之中。形形色色的Binder 对象以及星罗棋布的引用仿佛粘接各个应用程序的胶水，这也是 Binder 在英文里的原意。

当然面向对象只是针对应用程序而言，对于 Binder 驱动和内核其它模块一样使用 C 语言实现，没有类和对象的概念。**Binder 驱动** 为面向对象的进程间通信提供底层支持。


# Binder 通信模型
Binder 框架定义了四个角色：Server，Client，ServiceManager（以后简称SMgr）以及 Binder 驱动。
其中 Server，Client，SMgr 运行于用户空间，驱动运行于内核空间。这四个角色的关系和互联网类似：Server是服务器，Client 是客户终端，SMgr 是域名服务器（DNS），驱动是路由器。

## Binder 驱动

## ServiceManager 与实名 Binder


## Client 获得实名 Binder 的引用

## 匿名 Binder


# Binder 协议
Binder 协议基本格式是（命令+数据），使用 ioctl(fd, cmd, arg) 函数实现交互。命令由参数 cmd 承载，数据由参数 arg 承载，随 cmd 不同而不同。

## Binder 常用通信命令

## 写操作

## 从 Binder 读出数据

## 收发数据包结构

# Binder 的表述
Binder存在于系统以下几个部分中：

- 应用程序进程：分别位于 Server 进程和 Client 进程中
- Binder 驱动：分别管理为 Server 端的 Binder 实体和 Client 端的引用
- 传输数据：由于 Binder 可以跨进程传递，需要在传输数据中予以表述

## 在应用程序中的表述

Binder 本质上只是一种**底层通信方式**，和具体服务没有关系。为了提供具体服务，Server 必须提供一套接口函数以便 Client 通过远程访问使用各种服务。这时通常采用 Proxy 设计模式： 将接口函数定义在一个抽象类中， Server 和 Client 都会以该抽象类为基类实现所有接口函数，所不同的是 Server 端是真正的功能实现，而 Client 端是对这些函数远程调用请求的包装。如何将 Binder 和 Proxy 设计模式结合起来是应用程序实现面向对象 Binder 通信的根本问题。

### 在 Server 端的表述 – Binder实体

- 作为代理模式的基础,需要一个抽象接口封装 Server 所有功能,其中包含一些列抽象函数,留待 Server 和代理各自实现.
- 由于函数需要跨进程调用,需要为其一一编号,从而 Server 可以根据收到的编号决定调用哪个函数.
- Binder ,Server 端定义另一个 Binder 抽象类处理来自 Client 的 Binder 请求数据包,其中最重要的成员是抽象函数 onTransact . 该函数分析收到的数据包,调用响应的接口处理请求.

在 server 中以继承的方式,构建 Binder 的实体,实现所有的抽象函数,最重要的是 onTransact 函数,这个函数的输入是来自于 client 的 binder_transaction_data 结构的数据包。有个成员是 code ,包含了这次请求的接口函数编号.onTransact 将 case-by-case 地解析 code 值,从数据包取出函数参数,调用接口类中定义的函数(已被server 端实现).函数执行结束,如该需要返回值,就再构建一个 binder_transaction_data 包将返回数据包填入其中.

> 那么各个 Binder 实体的 onTransact 又是什么时候调用呢？这就需要驱动参与了。前面说过，Binder 实体须要以 Binder 传输结构 flat_binder_object 形式发送给其它进程才能建立 Binder 通信，而 Binder 实体指针就存放在该结构的 handle 域中。驱动根据 Binder 位置数组从传输数据中获取该 Binder 的传输结构，为它创建位于**内核中的 Binder 节点**，将 **Binder 实体指针**记录在该节点中。如果接下来有其它进程向该 Binder 发送数据，驱动会根据节点中记录的信息将 Binder 实体指针填入 binder_transaction_data 的 target.ptr 中返回给接收线程。接收线程从数据包中取出该指针， reinterpret_cast 成 Binder 抽象类并调用 onTransact() 函数。由于这是个虚函数，不同的 Binder 实体中有各自的实现，从而可以调用到不同 Binder 实体提供的 onTransact()。

### 在Client端的表述 – Binder引用
client 端的 binder 同样需要继承 server 提供的公共接口类,并实现公共函数.但这不是真正的实现,而是对远程函数调用的包装.将函数参数打包,通过 binder 向 server 发送申请并等待返回值.为此 client 端的 binder 还需要知道 binder 实体的相关系,即对 binder 实体的引用.

>由于继承了同样的公共接口类，Client Binder 提供了与 Server Binder 一样的函数原型，使用户感觉不出 Server 是运行在本地还是远端。Client Binder中，公共接口函数的包装方式是：创建一个 binder_transaction_data 数据包，将其对应的编码填入 code 域，将调用该函数所需的参数填入 data.buffer 指向的缓存中，并指明数据包的目的地，那就是已经获得的对 Binder 实体的引用，填入数据包的 target.handle 中。注意这里和 Server 的区别：实际上 target 域是个联合体，包括 ptr 和 handle 两个成员，前者用于接收数据包的 Server，指向 Binder 实体对应的**内存空间**；后者用于作为请求方的 Client，存放 Binder 实体的**引用**，告知驱动数据包将路由给哪个实体。数据包准备好后，通过驱动接口发送出去。经过 BC_TRANSACTION/BC_REPLY 回合完成函数的远程调用并得到返回值。


## Binder 在传输数据中的表述

Binder 可以塞在数据包的有效数据中越进程边界从一个进程传递给另一个进程，这些传输中的 Binder 用结构 flat_binder_object 表示.
无论是 Binder 实体还是对实体的引用都从属与某个进程，所以该结构不能透明地在进程之间传输，必须经过驱动翻译.

### flat_binder_object 成员

- type: 表明该 Binder 的类型: Binder 实体;Binder 的引用;文件形式的 Binder.
- flags: 该域只对第一次传递 Binder 实体时有效，因为此刻驱动需要在内核中创建相应的实体节点，有些参数需要从该域取出：处理本实体请求数据包的线程的最低优先级。表示该实体是否可以接收其它进程发过来的文件形式的 Binder .
- union {void *binder; signed long handle;} : 当传递的是 Binder 实体时使用 binder 域，指向 Binder 实体在应用程序中的地址。当传递的是 Binder 引用时使用 handle 域，存放 Binder 在进程中的引用号。
- cookie : 该域只对 Binder 实体有效，存放与该 Binder 有关的附加信息。

### 文件形式的 binder 

除了通常意义上用来通信的 Binder ，还有一种特殊的 Binder ：文件 Binder 。这种 Binder 的基本思想是：将文件看成 Binder 实体，进程打开的文件号看成 Binder 的引用。一个进程可以将它打开文件的文件号传递给另一个进程，从而另一个进程也打开了同一个文件，就象 Binder 的引用在进程之间传递一样。


>一个进程打开一个文件，就获得与该文件绑定的打开文件号。从Binder的角度，linux在内核创建的打开文件描述结构struct file是Binder的实体，打开文件号是该进程对该实体的引用。既然是Binder那么就可以在进程之间传递，故也可以用flat_binder_object结构将文件Binder通过数据包发送至其它进程，只是结构中type域的值为BINDER_TYPE_FD，表明该Binder是文件Binder。
而结构中的handle域则存放文件在发送方进程中的打开文件号。我们知道打开文件号是个局限于某个进程的值，一旦跨进程就没有意义了。这一点和Binder实体用户指针或Binder引用号是一样的，若要跨进程同样需要驱动做转换。驱动在接收Binder的进程空间创建一个新的打开文件号，将它与已有的打开文件描述结构struct file勾连上，从此该Binder实体又多了一个引用。新建的打开文件号覆盖flat_binder_object中原来的文件号交给接收进程。接收进程利用它可以执行read()，write()等文件操作


## Binder 在驱动中的表述

驱动是 Binder 通信的核心.
- 系统中所有的 Binder 实体以及每个实体在各个进程中的引用都登记在驱动中；
- 驱动需要记录 Binder 引用->实体之间多对一的关系；
- 为引用找到对应的实体；在某个进程中为实体创建或查找到对应的引用；
- 记录 Binder 的归属地（位于哪个进程中）；
- 通过管理 Binder 的强/弱引用创建/销毁 Binder 实体等等。

为了实现实名Binder的注册，系统必须创建第一只鸡–为 SMgr 创建的，用于注册实名 Binder 的 Binder 实体，负责实名 Binder 注册过程中的进程间通信。既然创建了实体就要有对应的引用：驱动将所有进程中的 0 号引用都预留给该 Binder 实体，即所有进程的 0 号引用天然地都指向**注册实名Binder专用的Binder**，无须特殊操作即可以使用 0 号引用来注册实名 Binder。接下来随着应用程序不断地注册实名 Binder ，不断向 SMgr 索要 Binder 的引用，不断将 Binder 从一个进程传递给另一个进程，越来越多的 Binder 以传输结构 - flat_binder_object的形式穿越驱动做跨进程的迁徙。由于 binder_transaction_data 中 data.offset 数组的存在，所有流经驱动的 Binder 都逃不过驱动的眼睛。Binder 将对这些穿越进程边界的 Binder 做如下操作：检查传输结构的 type 域，如果是 BINDER_TYPE_BINDER 或 BINDER_TYPE_WEAK_BINDER 则创建 Binder 的实体；如果是 BINDER_TYPE_HANDLE 或BINDER_TYPE_WEAK_HANDLE 则创建 Binder 的引用；如果是 BINDER_TYPE_HANDLE 则为进程打开文件，无须创建任何数据结构。随着越来越多的 Binder 实体或引用在进程间传递，驱动会在内核里创建越来越多的节点或引用，当然这个过程对用户来说是透明的。

### Binder 实体在驱动中的表述
驱动中的 Binder 实体也叫‘节点’，隶属于提供实体的进程，由 struct binder_node 结构来表示.

union {struct rb_node rb_node;struct hlist_node dead_node;}: 每个进程都维护一棵红黑树，以 Binder 实体在用户空间的指针，即本结构的 ptr 成员为索引存放该进程所有的 Binder 实体。这样驱动可以根据 Binder 实体在用户空间的指针很快找到其位于内核的节点。rb_node 用于将本节点链入该红黑树中。


>每个进程都有一棵红黑树用于存放创建好的节点，以 Binder 在用户空间的指针作为索引。每当在传输数据中侦测到一个代表 Binder 实体的 flat_binder_object ，先以该结构的 binder指针 为索引搜索红黑树；如果没找到就创建一个新节点添加到树中。由于对于同一个进程来说内存地址是唯一的，所以不会重复建设造成混乱。



### Binder 引用在驱动中的表述

Binder的引用也是驱动根据传输数据中的flat_binder_object创建的，隶属于获得该引用的进程，用struct binder_ref结构体表示：


# Binder 通信模型和 Binder 通信协议，了解 Binder 的设计需求

# Binder 在系统不同部分的 表述方式 和起的 作用

# Binder 在 数据接收端 的设计考虑，包括 线程池管理， 内存映射和等待队列管理等