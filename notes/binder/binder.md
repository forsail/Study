# Binder 简介

## Binder 背景
## Binder 是什么
## Binder 使用



## 概述
Java层中Binder实际上也是一个C/S架构，而且其在类的命名上尽量保持与Native层一致，因此可认为，Java层的Binder架构是Native层Binder架构的一个镜像。

- 涉及的类和接口
> Binder,Parcel,IBinder,BinderInternal

Binder类和BinderProxy类分别实现了IBinder接口。其中，Binder类作为服务端的Bn的代表，而BinderProxy作为客户端的Bp的代表。


IBinder接口类中定义了一个名为FLAG_ONEWAY的整型变量。当客户端利用Binder机制发起一个跨进程的函数调用时，调用方（即客户端）一般会 **阻塞**，直到服务端返回结果。这种方式和普通的函数调用是一样的。但是在调用Binder函数时，如果指明了FLAG_ONEWAY标志，则调用方只要把请求**发送到 Binder 驱动** 即可返回，而不用等待服务端的结果，这就是一种所谓的非阻塞方式。在Native层中，涉及的Binder调用基本都是阻塞的，但是在Java层的framework中，使用FLAG_ONEWAY进行Binder调用的情况非常多。

对于使用FLAG_ONEWAY的函数来说，客户端仅向服务端发出了请求，但是并不能确定服务端是否处理了该请求。所以，客户端一般会向服务端**注册一个回调（同样是跨进程的Binder调用）**，一旦服务端处理了该请求，就会调用此回调函数来通知客户端处理结果。当然，这种回调函数也大多采用FLAG_ONEWAY的方式。

## 初始化 Java 层 Binder 框架
 因为 Java 层的 Binder 是 native 层的一个镜像，所以需要在一开始的时候建立这种关系。

在 Binder.cpp 下有个 register_android_os_Binder 专门用来搭建 Java Binder 和 Native Binder 之间的关系。

在这个函数中，分别调用了下面的四个函数，完成四个类的初始化工作。
- int_register_android_os_Binder:
- int_register_android_os_BinderInternal
- int_register_android_os_BinderProxy
- int_register_android_os_Parcel

int_register_android_os_BinderInternal 的工作内容和 int_register_android_os_Binder 的工作内容类似，包括以下两方面：
- 获取一些有用的methodID和fieldID。这表明JNI层一定会向上调用Java层的函数。
- 注册相关类中native函数的实现。
