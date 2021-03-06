#[dagger的笔记](http://google.github.io/dagger/)

# 概念
非自己主动初始化依赖，而通过外部来传入依赖的方式，我们就称为依赖注入。
像将依赖的对象通过构造函数的参数传入，也算是依赖注入。

##对构造函数进行注解
使用@Inject这个注解对一个构造函数进行注解的时候，表示dagger应该使用这个构造函数来生成一个实例。
当需要这个类的一个实例的时候，dagger将会获取必要的参数来生成这个对象。


##对属性值进行注解
假如存在注解需要注入对象，但是没有对应的注解的构造函数，
dagger会对那些需要被注入的属性进行注入，但是并不是创建新的实例。
也可以用注解添加一个无参的构造函数来通知dagger创建新的实例。
dagger还支持对方法的注解，尽管属性或者构造函数更加典型。
对于那些没用注解的类，是不能被dagger正确的构造的。

##注解不适用的情况
1.对于接口是不能注解；
1.对于第三方的类是不能注解的；
1.可配置的对象必须被配置好(不知道啥意思)
对于以上的三种情况，dagger是比较尴尬的，体验也是比较糟糕的，所以可以使用 @Provides这个注解来满足要求。
这个方法的返回值是那些被依赖的类型。
具体做法是在一个Module类中通过 @Provides来注解一个方法。
约定俗成，被 @Provides注解的方法名以provide开头，类名以Module 结束。


#[概念](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0519/2892.html)
@Inject: 通常在需要依赖的地方使用这个注解。换句话说，你用它告诉Dagger这个类或者字段需要依赖注入。这样，Dagger就会构造一个这个类的实例并满足他们的依赖。
@Module: Modules类里面的方法专门提供依赖，所以我们定义一个类，用@Module注解，这样Dagger在构造类的实例的时候，就知道从哪里去找到需要的 依赖。modules的一个重要特征是它们设计为分区并组合在一起（比如说，在我们的app中可以有多个组成在一起的modules）。
@Provide: 在modules中，我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖。
@Component: Components从根本上来说就是一个注入器，也可以说是@Inject和@Module的桥梁，它的主要作用就是连接这两个部分。 Components可以提供所有定义了的类型的实例，比如：我们必须用@Component注解一个接口然后列出所有的@Modules组成该组件，如 果缺失了任何一块都会在编译的时候报错。所有的组件都可以通过它的modules知道依赖的范围。
@Scope: Scopes可是非常的有用，Dagger2可以通过自定义注解限定注解作用域。后面会演示一个例子，这是一个非常强大的特点，因为就如前面说的一样，没 必要让每个对象都去了解如何管理他们的实例。在scope的例子中，我们用自定义的@PerActivity注解一个类，所以这个对象存活时间就和 activity的一样。简单来说就是我们可以定义所有范围的粒度(@PerFragment, @PerUser, 等等)。
@Qualifier: 当类的类型不足以鉴别一个依赖的时候，我们就可以使用这个注解标示。例如：在Android中，我们会需要不同类型的context，所以我们就可以定义 qualifier注解“@ForApplication”和“@ForActivity”，这样当注入一个context的时候，我们就可以告诉 Dagger我们想要哪种类型的context。


## 需要着重注意的点
1. inject 可以注解一个构造函数，可以进行属性注解，前者表示生产的方式，后者表示这个属性通过注解获得。
1. module 就相当于一个生产车间，里面提供了各式各样的产生类实例的方法。
1. component ，是个类和需要类实例之间的桥梁，负责将产生的实例注入到需要实例的地方去。
1. provide，是 module 中， 修饰提供类实例方法的。
1. scope，对于依赖而言，被依赖的有范围，则自身也需要有范围，必须要不一样，即没有 scope 的不能依赖有 scope的。
1. 一个 component 不能有多个 scope。
1. @Qualifier 可以用来区分提供相同类型实例的 provide 方法,注意， 这个注解不能应用于构造函数
1. component 是一个依赖链，当在当前的 component 中找不到对象的时候，会顺着这个依赖向下寻找，所以，被依赖的component必须显式的声明可以对外暴露的类型。
1. @Inject 可以标记成员变量，但是这些成员变量要求是包级可见，也就是说 @Inject 不可以标记 private 类型的成员变量。
1. component 可以注入任意个类，例如 application 的 component，我们可以直接注册在任何一个 activity 中，也可以注册在application中。
1. 对于 module 中的 activity 对象，可以通过 module 的有参构造函数传递进去，外界需要，可以通过 provide 注入，application 同理。

