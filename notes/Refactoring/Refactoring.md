# Refactoring

## Tip1

- 如果发现添加一个特性的时候,而代码结构是你无法很方便的达成目的,那就先重构那个程序,使得新特性容易添加,再添加新特性.

- 重构之前先检查,是否有可靠的测试机制.

- 任何一个傻瓜都能写出计算机可以识别的代码.唯有写出人类容易理解的代码才是优秀的程序员.



## Tip2

-  重构(名词):对软件内部结构的调整,目的不是在改变软件的可观察行为的前提下,提高其可理解性,降低其修改成本.

-  重构(动词):使用一系列重构手法,在不改变软件可观察行为的前提下,调整其结构.

- 事不过三,三则重构.




## 章节八 重新组织数据

### Self Encapsulate Field（自封装值域）

- 简述
你直接访问一个值域（field），但与值域之间的耦合关系逐渐变得笨拙。为这个值域建立取值/设值函数（getting and setting methods ），并且只以这些函数来访问值域。
- 动机
「间接访问变量」的好处是，subclass得以通过「覆写一个函数」而改变获取数据的途径；它还支持更灵活的数据管理方式。
尤其在想访问superclass中的一个值域，却又想在subclass中将「对这个变量的访问」改为一个计算后的值，这就是最该使用Self Encapsulate Field的时候。

### Replace Data Value with Object（以对象取代数据值）

- 简述
将一个对象中的额外数据封装成一个对象。
- 动机
宿主对象中的数据项所对应的feature方法越来越多，就需要将这些数据项和其相应的方法封装成一个对象。


### Change Value to Reference（将实值对象改为引用对象）
- 简述
你有一个class，衍生出许多相等实体（equal instances），你希望将它们替换为单一对象。
- 动机
有大量的相同数值的值对象存在，改用引用对象，可以减少复杂性。
- 做法
定义一个创建对象的工厂方法（此处之factory method不等同于GoF在《Design Patterns》书中提出的Factory Method。为避免混淆，读者应该将此处的factory method理解为"Creational Method"，亦即「用以创建某种实体」的函数，这个概念包含GoF的Factory Method，而又比Factory Method广泛。），根据实际的数值创建不同的对象。

### Change Reference to Value （将引用对象改为实值对象）
- 简述
将实值对象改为引用对象 和 将 引用对象改为实值对象，两个是比较难以做抉择。
- 动机
因为引用对象存在同步的问题，会造成内存引用之间的错综复杂。当引用对象在一个场景下被修改了，必须保证其他引用地方也及时的收到了通知。这个会变得复杂，不如换成值对象。


### Replace Array with Object（以对象取代数组）
- 简述
你有一个数组（array），其中的元素各自代表不同的东西。以对象替换数组。对于数组中的每个元素，以一个值域表示之。
- 动机
这种状况比较少，就是实现约定好数组中的每一位代表何种涵义。

### Duplicate Observed Data（复制「被监视数据」） TODO
- 简述
[注:] 所谓 presentation class，用以处理「数据表现形式」；所谓domain class，用以处理业务逻辑。
有一些 domain class 置身于GUI控件中，而 domain method 需要访问之。将该笔数据拷贝到一个 domain object 中。建立一个 Observer 模式，用以对 domain object 和 GUI object 内的重复数据进行同步控制（sync.）。
- 动机
如果代码是以双层（two-tiered）方式开发，业务逻辑（business logic）被内嵌于用户界面（UI）之中，你就有必要将行为分离出来。其中的主要工作就是函数的分解和搬移。但数据就不同了：你不能仅仅只是移动数据，你必须将它复制到新建部位中，并提供相应的同步机制。

一个分层良好的系统，应该将处理用户界面（UI）和处理业务逻辑（business logic）的代码分开。之所以这样做，原因有以下几点：
1. 你可能需要使用数个不同的用户界面来表现相同的业务逻辑；如果同时承担两种责任，用户界面会变得过分复杂；
2. 与GUI隔离之后，domain class的维护和演化都会更容易；你甚至可以让不同的开发者负责不同部分的开发。

尽管你可以轻松地将「行为」划分到不同部位，「数据」却往往不能如此。同一笔 数据有可能既需要内嵌于GUI控件，也需要保存于domain model里头。
自从MVC（Model-View-Controller）模式出现后，用户界面框架都使用多层系统（multitiered system）来提供某种机制，使你不但可以提供这类数据，并保持它们同步（sync.）。


- 作法:

* 修改presentation class，使其成为 domain class 的 Observer[GoF]。
* 如果尚未有domain class，就建立一个。
* 如果没有「从presentation class到domain class的关联性（link）， 就将domain class保存于咖presentation class的一个值域中。
* 针对GUI class内的domain data，使用Self Encapsulate Field 。


### Change Unidirectional Association to Bidirectional（将单向关联改为双向）
- 简介
两个classes都需要使用对方特性，但其间只有一条单向连接（one-way link）。添加一个反向指针，并使修改函数（modifiers）能够同时更新两条连接。

- 动机
开发初期，你可能会在两个classes之间建立一条单向连接，使其中一个可以引用另一个class。随着时间推移，你可能发现referred class 需要得到其引用者（某个object）以便进行某些处理。也就是说它需要一个反向指针。

### Change Bidirectional Association to Unidirectional（将双向关联改为单向）

- 简介
两个类之间有双向关联，但其中一个class如今不再需要另一个class的特性。
- 动机
双向关联（bidirectional associations）很有用，但你也必须为它付出代价，那就是「维护双向连接、确保对象被正确创建和删除」而增加的复杂度。而且，由于很多程序 员并不习惯使用双向关联，它往往成为错误之源。
双向关联也迫使两个classes之间有了相依性。对其中任一个class的任何修 改，都可能引发另一个class的变化。如果这两个classes处在不同的package中， 这种相依性就是packages之间的相依。过多的依存性（inter-dependencies）会造就紧耦合（highly coupled）系统，使得任何一点小小改动都可能造成许多无法预知的后果。
只有在你需要双向关联的时候，才应该使用它。如果你发现双向关联不再有存在价值，就应该去掉其中不必要的一条关联。

### Replace Magic Number with Symbolic Constant（以符号常量/字面常量取代魔法数）
- 简介
你有一个字面数值（literal number ），带有特别含义。
- 动机
其实就是新建一个常量表达一个有特殊涵义的数值。

### Encapsulate Field（封装值域）
- 简介
你的class中存在一个public值域。将它声明为private，并提供相应的访问函数（accessors）。
- 动机
面向对象的首要原则之一就是封装（encapsulation），或者称为「数据隐藏」（data hidding）。按此原则，你绝不应该将数据声明为public，否则其他对象就有可能访问甚至修改这项数据，而拥有该数据的对象却毫无察觉。这就将数据和行为分开了（不妙）。

### Encapsulate Collection（封装集合）
- 简介
有个函数返回一个集合。让这个函数返回该群集的一个只读对象，并在这个class中提供（add/remove）集合元素的函数。
- 动机
取值函数（getter）不该返回集合自身，因为这将让用户得以修改集合内容而群集拥有者却一无所悉。这也会对用户暴露过多「对象内部数据结构」的信息。

### Replace Record with Data Class（以数据类取代记录）
- 简介
任何有用行为（函数〕，只有数据和访问函数。
- 动机
就是用Javabean来表示记录。

### Replace Type Code with Class（以类取代型别码）
- 简介
class 之中有一个数值型别码，但它并不影响 class 的行为。以一个新的 class 替换该数值型别码（type code）。
注意，这个类别码不能影响 class 的行为。
- 动机
虽然类别码可以用常量替代，但终究只是个数值，任何接受type code 作为参数的函数，所期望的实际上是一个数值，无法强制使用符号名。这会大大降低代码的可读性。
如果把那样的数值换成一个class ，编译器就可以对这个class 进行型别检验。只要为这个class 提供 factory methods ，你就可以始终保证只有合法的实体才会被创建出来，而且它们都会被传递给正确的宿主对象。

### Replace Type Code with Subclasses（以子类取代型别码）
- 简介
你有一个不可变的（immutable）type code ，它会影响class 的行为。以一个subclass 取代这个type code。
- 动机
如果宿主类中出现 了「只与具备特定type code 之对象相关」的特性,type code 会影响宿主类的行为，那么最好的办法就是借助多态（polymorphism ）来处理变化行为。这种情况的标志就是像switch 这样的条件式。

以type code 的宿主类为base class，并针对每一种type code 各建立一个subclass 。

以下两种情况你不能那么做：
1. type code 值在对象创建之后发生了改变；
2. 由于某些原因，type code 宿主类已经有了subclass 。如果你恰好面临这两种情况之一，就需要使用Replace Type Code with State/Strategy 。

把「对不同行为的了解」从class 用户那儿转移到了class 自身。如果需要再加入新的行为变化，我只需添加subclass 一个就行了。

- 作法:
* 使用Self-encapsulate Field 将type code 自我封装起来。
* 如果type code 被传递给构造函数，你就需要将构造函数换成factory method。
* 为type code 的每一个数值建立一个相应的subclass 。在每个subclass 中覆写（override）type code的取值函数.



### Replace Type Code with State/Strategy（以State/strategy 取代型别码）
- 简介
你有一个type code ，它会影响class 的行为，但你无法使用subclassing。以state object （专门用来描述状态的对象）取代type code 。
- 动机
本项重构和Replace Type Code with Subclasses 很相似，但如果「type code 的值在对象生命期中发生变化」或「其他原因使得宿主类不能被subclassing 」，你也可以使用本重构。

- 作法
* 使用Self-encapsulate Field 将type code 自我封装起来。
* 新建一个class ，根据type code 的用途为它命名。这就是一个state object。
* 为这个新建的class 添加subclass ，每个subclass 对应一种type code 。
* 在superclass 中建立一个抽象的查询函数（abstract query ），用以返回type code 。 在每个subclass 中覆写该函数，返回确切的type code 。


### Replace Subclass with Fields（以值域取代子类）
- 简介
你的各个subclasses 的惟一差别只在「返回常量数据」的函数身上。修改这些函数，使它们返回superclass 中的某个（新增）值域，然后销毁subclasses 。

- 动机
建立subclass 的目的，是为了增如新特性，或变化其行为。有一种变化行为称为「常量函数」，它们会返回一个硬编码值。这东西有其用途：你可以让不同的subclasses 中的同一个访问函数返回不同的值。
你可以在superclass 中将访问函数声明为抽象函数， 并在不同的subclass 中让它返回不同的值。

尽管常量函数有其用途，但若subclass 中只有常量函数，实在没有足够的存在价值。 你可以在中设计一个与「常量函数返回值」相应的值域，从而完全去除这样的subclass 。
如此一来就可以避免因subclassing 而带来的额外复杂性。

- 作法
将常量函数所设置的字段声明为super class中的一个field，并提供相应的构造函数，并且在super class中创建"Creational Method"。
