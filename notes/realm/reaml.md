# Realm Introduction

Realm Java 让你能够高效地编写 app 的模型层代码，保证你的数据被安全、快速地存储。

# 快速简介

## 安装

1. 第一步： 在项目的 build.gradle 文件中添加如下 class path 依赖。
```groovy
buildscript {repositories {jcenter()}dependencies {classpath "io.realm:realm-gradle-plugin:2.1.1"}}
```

2. 第二步： 在 app 的 build.gradle 文件中应用 realm-android 插件。

```groovy
apply plugin: 'realm-android'
```
##  ProGuard
Realm 已经集成了 ProGuard 的配置。你不需要针对 Realm 做 ProGuard 的改动。

## Realm浏览器
目前这个浏览器只有mac版本的。

# 概念

## model
Realm 数据模型定义需要继承自 RealmObject 类。Realm 数据模型不仅仅支持 private 成员变量，你还可以使用 public、protected 以及自定义的成员方法。

## 字段类型
Realm 支持以下字段类型：boolean、byte、short、int、long、float、double、String、Date和byte []。整数类型 short、int 和 long 都被映射到 Realm 内的相同类型（实际上为 long ）。再者，还可以使用 RealmObject 的子类和 RealmList<? extends RealmObject> 来表示模型关系。Realm 对象中还可以声明包装类型（boxed type）属性，包括：Boolean、Byte、Short、Integer、Long、Float和Double。通过使用包装类型，可以使这些属性存取空值（null）。

### @Required修饰类型和空值（null）
对于某些属性，并不能为null，可以使用注解@Required 告诉 Realm 强制禁止空值（null）被存储。这个注解只对非基本数据类型起作用。基本数据类型本身不可能为空。

### 忽略的属性
注解 @Ignore 意味着一个字段不应该被保存到 Realm。这个注解的属性是告诉Realm这个属性不需要存储到数据库中。

### Auto-Updating Objects
双向自动更新，RealmObjects 是在底层就实现了同步更新，就是说当数据改变以后，无论是查询结果还是已有的RealmObject 对象都会自动更新相关的字段。
举例来说，假设你的 Activity 或者 Fragment 依赖于某个 RealmObject 或者 RealmResults，你无需担心何时去刷新或者重新获取它们以更新 UI——它们会自动更新。
你可以通过订阅 Realm notifications 来得知 Realm 的数据在何时被更新从而刷新你的 UI。

### Index
注解 @Index 会为字段增加搜索索引。这会导致插入速度变慢，同时数据文件体积有所增加，但能加速查询。因此建议仅在需要加速查询时才添加索引。目前仅支持索引的属性类型包括：String、byte、short、int、long、boolean和Date。

### 主键 (primary keys)
@PrimaryKey 可以用来定义字段为主键，该字段类型必须为字符串（String）或整数（short、int 或 long）以及它们的包装类型（Short、Int 或 Long）
使用主键会对性能产生影响。创建和更新对象将会慢一点，而查询则会变快。很难量化这些性能的差异，因为性能的改变跟你数据库的大小息息相关。

### 定制对象（Customizing Objects）
你几乎可以把 RealmObject 当作 POJO 使用。只需扩展 RealmObject，将相应属性声明为 public。不需要 setter 和 getter， 而是直接访问属性。当然也可以有 setter 和 getter 方法。createObject() 和 copyToRealm() 可以帮助你创建一个托管给 Realm 的 类对象。

### 限制
- 目前不支持 final、transient 和 volatile 修饰的成员变量。
- Realm 数据模型不可以继承自除了 RealmObject 以外的其它对象。你可以选择不声明默认无参数构造器，但是如果你声明了，那么该构造器必须为空构造器。这是目前 Realm 的一个限制。但你可以自由添加任意其它的构造器。

## RealmModel 接口

除直接继承于 RealmObject 来声明 Realm 数据模型之外，还可以通过实现 RealmModel 接口并添加 @RealmClass 修饰符来声明。

## Relationships 关系
任意两个 RealmObject 可以相互关联，直接将对象声明为属性即可。RealmObject 之间的关联总体来说并不怎么消耗系统开销。Realm 对关系对象的处理非常高效并且节约内存。

### 多对一
你只需要简单地声明一个 Realm 模型类的属性即可。

### 多对多
你可以通过使用 RealmList<T> 为一个对象关联0或多个其它对象。

RealmList 是 Realm 模型对象的容器，其行为与 Java 的普通 List 近乎一样。同一个 Realm 模型对象可以存在于多个 RealmList 中。同一个 Realm 模型对象可以在同一个 RealmList 中存在多次。你可以使用 RealmList 来表现一对多和多对多的数据关系。

设置一个类型为 RealmList 的属性为空值（null）会清空该列表，即列表长度变为 0。但并不会删除列表中的任何 RealmObject。RealmList 的获取器（getter）永不会返回 null。其返回对象永远是一个 RealmList 实例，但其长度有可能为0。

### 关联查询

```java
realmresults<user> r1 = realm.where(user.class)
                             .equalto("dogs.name", "fluffy")
                             .equalto("dogs.color", "brown")
                             .findall();


realmresults<user> r2 = realm.where(user.class)
                             .equalto("dogs.name", "fluffy")
                             .findall()
                             .where()
                             .equalto("dogs.color", "brown")
                             .findall();
                             .where()
                             .equalto("dogs.color", "yellow")
                             .findall();
```

## 写入
在任何时间都可以对对象进行访问和查询（读取事务是隐式的）。
所有的写操作（添加、修改和删除对象），必须包含在写入事务（transaction）中。写入事务可以提交或取消。在提交期间，所有更改都将被写入磁盘，并且，只有当所有更改可以被持久化时，提交才会成功。通过取消一个写入事务，所有更改将被丢弃。使用写入事务，会确保你的数据的一致性。
```java
// Obtain a Realm instance
Realm realm = Realm.getDefaultInstance();
// 开启事务
realm.beginTransaction();

//... add or update objects here ...
// 提交事务
realm.commitTransaction();
```
写入事务之间会互相阻塞，如果一个写入事务正在进行，那么其他的线程的写入事务就会阻塞它们所在的线程。同时在 U I线程和后台线程使用写入事务有可能导致 ANR 问题。可以使用 异步事务（async transactions）以避免阻塞 UI 线程。.

Realm 数据文件不会因为程序崩溃而损坏。当有异常在事务块中被抛出时，当前事务中所做出的数据修改会被丢弃。如果在该情况下程序需要继续运行，那么请调用 cancelTransaction() 来中止事务，或者使用 executeTransaction() 来执行事务。


### 创建对象
由于 Realm 对象都强依赖于 Realm，它们应该直接通过 Realm 被实例化。

或者你可以先创建一个对象的实例，并在之后使用 realm.copyToRealm() 添加。Realm 对象支持多个构造函数，只要其中之一是 **公共无参数构造函数** 即可。

当使用 realm.copyToRealm() 时，请注意只有返回的对象是由 Realm 管理的，这非常重要。对原始对象（umanaged Object）的任何改变都不会写入 Realm.

### 事务执行块（Transaction blocks）
除手动调用 realm.beginTransaction()、realm.commitTransaction() 和 realm.cancelTransaction() 之外你可以使用 realm.executeTransaction() 方法，它会自动处理写入事物的开始和提交，并在错误发生时取消写入事物。

### 异步事务（Asynchronous Transactions）
事务会相互阻塞其所在的线程，在后台线程中开启事务进行写入操作可以有效避免 UI 线程被阻塞。通过使用异步事务，Realm 会在后台线程中进行写入操作，并在事务完成时将结果传回 **调用线程**。

异步事务调用会返回一个 RealmAsyncTask 对象。当你退出 Activity 或者 Fragment 时可以使用该对象取消异步事务。如果你在回调函数中更新 UI，那么忘记取消异步事务可能会造成你的应用崩溃。

### 更新字符串和 byte 数组
Realm 的写操作针对的是整个字符串或 byte 数组属性而非该属性中的单独元素。

## 查询
Realm 中的所有读取（包括查询）操作都是延迟执行的，且数据绝不会被拷贝。
Realm 的查询引擎使用 Fluent interface 来构造多条件查询。

查询返回一个 RealmResults 实例，这些对象 **并非拷贝**，也就是说你得到的是一个匹配对象引用的列表，你对匹配对象所有的操作都是直接施加于它的原始对象。
当查询没有任何匹配时，返回的 RealmResults 对象将不会为 null，取而代之的是它的 size() 方法将返回 0。
修改或删除 RealmResults 中任何一个对象都必须在写入事务中完成。

### 查询条件

Realm 支持以下查询条件：
- between()、greaterThan()、lessThan()、greaterThanOrEqualTo() 和 lessThanOrEqualTo()
- equalTo() 和 notEqualTo()
- contains()、beginsWith() 和 endsWith()
- isNull() 和 isNotNull()
- isEmpty() 和 isNotEmpty()

### 修饰符
字符串查询条件可以通过使用 Case.INSENSITIVE 修饰符来忽略字母 A-Z 和 a-z 的大小写。

### 逻辑运算符
每个查询条件都会被被隐式地被逻辑和（&）组合在一起，而逻辑或（or）需要显式地去执行 or()。
你也可以将查询条件组合在一起，使用 beginGroup()（相当于左括号）和 endGroup()（相当于右括号）.
此外，也可以用 not() 否定一个条件。该 not() 运算符可以与 beginGroup()/endGroup() 一起使用来否定子条件。

### 排序
当你执行完查询获得结果后，可以对它进行排序.


### 链式查询

因为查询结果并不会被复制，且在查询提交时并不会被执行，你可以链式串起查询并逐步进行分类筛选：
请注意，查询链最终是建立在 RealmResults 上而非 RealmQuery。如果你在某存在的 RealmQuery 上添加更多的查询条件，那么你在修改查询本身，而非查询链。

### 查询结果的自动更新（Auto-Updating Results）

RealmResults 是对其所包含数据的自动更新视图，这意味着它永远**不需要被重新查询获取**。数据对象的改变会在下一次 Looper 事件中被反映到相应的查询结果.

### 按类型检索对象
从 Realm 中检索对象的最基本方法是 realm.where(Foo.class).findAll()，它返回了包含被查询模型类的所有对象的 RealmResults。

### 聚合
自带的一些聚合方法.

### 迭代
RealmResults支持for循环.RealmResults的自动更新会通过looper事件触发,但在事件到来之前,某些元素有可能不再满足查询条件或则已被删除.
为避免该问题，可以使用 RealmResults 的 deleteFromRealm() 方法,而非Object的deleteFromRealm.

### 删除
你可以从查询结果中删除数据.
RealmResults 的 deleteFromRealm() 方法;或者Object的deleteFromRealm.
都需要放在事务当中.


### 异步查询
Realm 的大部分查询都非常快——快到可以使用在UI线程中而感觉不到延迟。但如果需要进行非常复杂的查询或者在大量数据中进行查询，那么使用后台线程进行查询将会是一个不错的主意。

#### 创建异步查询
```java
RealmResults<User> result = realm.where(User.class)
                              .equalTo("name", "John")
                              .or()
                              .equalTo("name", "Peter")
                              .findAllAsync();
```
请注意，这里的调用并不会阻塞，而是立即返回一个 RealmResults<User>。这很类似于标准 Java 中 Future 的概念。查询将会在后台线程中被执行，当其完成时，之前返回的 RealmResults 实例会被更新。
如果你希望当查询完成、RealmResults 被更新时获得通知，你可以注册一个 RealmChangeListener。这个监听器会在 RealmResults 被更新时被调用（通常是在事务被提交后）。

#### 注册回调
请在退出 Activity 或者 Fragment 时移除监听器的注册以避免内存泄漏。


#### 检查查询是否完成
isLoaded方法可以判断数据是否被加载完成,同步查询返回的 RealmResults 实例的 isLoaded 方法会永远返回 true。

#### 强制装载异步查询
调用RealmResults 的load方法,将会阻塞当前线程，使查询变成同步（与 Future.get() 类似的概念）。


#### 非 Looper 线程
你可以在 Looper 线程中使用异步查询。异步查询需要使用 Handler 来传递查询结果。在没有 Looper 的线程中使用异步查询会导致 IllegalStateException 异常被抛出。


## Realms
Realm(s) 是我们对数据库的称谓：它包含多个不同的对象，并对应磁盘中的一个文件。在使用之前，需要对 Realm 库进行初始化操作,你需要提供一个安卓的 Context 对象来对 Realm 进行初始化。初始化操作只要进行一次。继承 Application 并在重载的 onCreate() 方法中进行初始化是个不错的主意。

### 默认的 Realm
你可能已经注意到，我们总是通过Realm.getInstance(this)来访问我们已初始化的realm变量。该静态方法会为你的**当前线程**返回一个Realm实例，它对应了你Context.getFilesDir()目录中的default.realm文件。

请务必注意到Realm的实例是线程单例化的，也就是说，在同一个线程内多次调用静态方法获得针对同路径的Realm，会返回同一个Realm实例。

### 配置 Realm
一个典型的配置:
```java
// The RealmConfiguration is created using the builder pattern.
// The Realm file will be located in Context.getFilesDir() with name "myrealm.realm"
RealmConfiguration config = new RealmConfiguration.Builder()
  .name("myrealm.realm")
  .encryptionKey(getKey())
  .schemaVersion(42)
  .modules(new MySchemaModule())
  .migration(new MyMigration())
  .build();
// Use the config
Realm realm = Realm.getInstance(config);

```
很重要的一点是 Realm 实例是**线程单例化**的，也就是说多次在同一线程调用静态构建器会返回同一 Realm 实例。


### 默认 RealmConfiguration

RealmConfiguration可以保存为默认配置。通过在自定义的Application设置默认的Realm配置，可以使你在代码中的其他地方更加方便地创建针对该默认配置的Realm。

```java
public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // The Realm file will be located in Context.getFilesDir() with name "default.realm"
    RealmConfiguration config = new RealmConfiguration.Builder().build();
    Realm.setDefaultConfiguration(config);
  }
}

public class MyActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Realm realm = Realm.getDefaultInstance();
    // ... Do something ...
    realm.close();
  }
}

```


### In-Memory Realm
定义一个非持久化的、存在于内存中的 Realm 实例：
```java
RealmConfiguration myConfig = new RealmConfiguration.Builder()
    .name("myrealm.realm")
    .inMemory()
    .build();
```
这样就可以创建一个存在于“内存中的” Realm。“内存中的”Realm 在内存紧张的情况下仍有可能使用到磁盘存储，但是这些磁盘空间都会在Realm实例完全关闭的时候被释放。

请注意使用同样的名称同时创建“内存中的”Realm 和常规的（持久化）Realm 是不允许的。

当某个“内存中的”Realm 的**所有实例**引用都被释放，该 Realm 下的数据也同时会被清除。建议在你的**应用生命周期**中保持对“内存中的” Realm 实例的引用以避免非期望的数据丢失。

### Dynamic Realm
对于普通的 Realm 来说，数据模型被定义成了 RealmObject 的子类。这样做保证了类型安全，但有时候某些数据模型在编译期是无法获得的。
DynamicRealm 是普通 Realm 的一个变种。它可以在没有 RealmObject 子类的情况下操作 Realm 数据。其对数据的访问是**基于字符串**而 **非 RealmObject**的定义。
创建 Dynamic Realm 使用与创建普通 Realm 相同的RealmConfiguration，但是它的创建过程会忽略对 schema、migration以及 schema 版本的检查。

### 关闭Realm实例
Realm 实现了 Closeable 接口以便与释放 native 内存和文件描述符，请务必在使用完毕后关闭 Realm 实例。
Realm 实例是基于**引用计数**的, 也就是说假设你在同一个线程中调用了 getInstance() **两次**，你需要同样调用 close() **两次**以关闭该实例。举例来说，如果你需要实现 Runnable，简单地在函数开始的时候调用 getInstance()，在函数结束的时候调用 close() 即可！
对于UI线程，你可以选择在 onDestroy() 方法内调用 realm.close()。
```java
public class MyThread extends Thread {

    private Realm realm;

    public void run() {
        Looper.prepare();
        try {
            realm = Realm.getDefaultInstance();
            //... Setup the handlers using the Realm instance ...
            Lopper.loop();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
```


### 自动更新（Auto-Refresh）
如果 Realm 实例存在于一个带有 Looper 的线程，那么这个 Realm 实例即具有自动更新的功能。这意味这如果发生了 Realm 数据库的变化，那么该 Realm 实例会在**下一个事件循环（event loop）中自动**更新。这个便捷的功能使你不必花费太多的精力就能保证的UI与数据的实时同步。

如果 Realm 的实例所在线程没有绑定 Looper，那么该实例不会被更新直到你手动调用 waitForChange() 方法。请注意，不更新 Realm 以保持对旧数据的引用会造成而外的磁盘和内存开销。这也是为什么要在线程结束时调用 close() 关闭 Realm 实例的一个重要原因。

如果你想确定当前 Realm 实例是否有自动更新功能，可以通过调用 isAutoRefresh() 方法查询。


## 线程（Threading）
得益于对象和查询的即时更新特性，不需要担心数据在多线程时的一致性和效率问题。

可以实时在**不同线程中读取和写入 Realm 对象**，不用担心其它线程会对同一对象进行操作。需要在改变对象时使用事务，在另一线程中指向同一对象的数据会被即时更新（更新会在下一次事件循环时进行）。

唯一局限是你不能随意**跨线程传递 Realm 对象**。如果你在另一线程使用同一对象，请在另外线程使用查询**重新获得该对象**。请谨记所有的 Realm 对象都会在不同线程中保持更新——Realm 会在数据改变时通知你。

### Realm 线程实例
在一个后台线程中从远端获取新用户并将它们存储到 Realm 中。但后台线程存储新用户时，UI 线程中的数据会被自动更新。UI 线程会通过 RealmChangeListener 得到通知，这时 UI 线程应刷新相应的控件。因为 Realm 的自动更新特性，无需重新查询数据。

### 跨线程使用 Realm
请谨记：Realm、RealmObject 和RealmResults 实例都**不可以跨线程使用**。但是你可以使用异步查询和异步事务来将**部分操作放入后台线程进行**，待完成时**调用线程**被通知以获取结果。

当你需要跨线程访问同一部分数据时，只需简单地在该线程**重新**获取一个 Realm 实例.

## Schemas
这块待定.

## JSON
你可以直接将 JSON 对象添加到 Realm 中，这些 JSON 对象可以是一个 String、一个 JSONObject 或者是一个 InputStream。Realm 会忽略 JSON 中存在但未定义在 Realm 模型类里的字段。单独对象可以通过 Realm.createObjectFromJson() 添加。对象列表可以通过 Realm.createAllFromJson() 添加。

## 通知（Notifications）
Listener 只工作于 Looper 线程。对于非 Looper 线程请使用 Realm.waitForChange()。

当后台线程向 Realm 添加数据，你的 UI 线程或者其它线程可以添加一个监听器来获取数据改变的通知。监听器在 Realm 数据改变的时候会被触发。除了在 Realm 实例上添加监听器以外，你还可以在 RealmObject 和 RealmResults 实例上添加监听器。你可以通过这样的方式来监视对象和查询结果的改变。另外，当监听回调函数被调用时，相应的数据已经被更新，你不需要去做刷新操作。


## 迁移（Migrations）

面对数据结构发生改变的时候,会抛出Migration的异常,需要自己定义RealmMigration对象去处理这个异常.

## 加密
Realm 文件可以通过传递一个512位（64字节）的密钥参数给 Realm.getInstance().encryptionKey() 来加密存储在磁盘上。


## 与 Android 相关
Realm 可以无缝地引入安卓开发。只需要谨记 RealmObject 的线程限制。

### 适配器（Adapter）
需要依赖realm官方的adater配合使用.

### Intents
因为你不可以直接通过 intent 传递 RealmObject，我们建议你只传递 RealmObject 的标识符。比如传递对象的ID到下个页面当中,而后通过查询.

### Android Framework 多线程 API 相关
AsyncTask 的 doInBackground() 方法会运行在一个后台线程。IntentService 的 onHandleIntent(Intent intent) 方法会运行在一个后台工作线程。

如果你需要在这些方法中使用 Realm，请在对 Realm 的调用结束后关闭 Realm 实例。


## 对其它库的支持

### GSON
GSON 是 Google 开发的 JSON 处理库。GSON 与 Realm 可以无缝配合使用。

### 序列化（Serialization）
你有时需要序列化与反序列化一个 Realm 对象以便与其它库（比如 Retrofit）相配合。因为 GSON使用成员变量值而非 getter 和 setter ，所以你无法通过 GSON 的一般方法来序列化 Realm 对象。
你需要为 Realm 模型对象自定义一个 JsonSerializer 并且将其注册为一个 TypeAdapter。


### 数组（Primitive lists）
待定

### Retrofit
ealm 可以与 Retrofit 1.x 和 2.x 无缝配合工作。但请注意 Retrofit 不会自动将对象存入 Realm。你需要通过调用 Realm.copyToRealm() 或 Realm.copyToRealmOrUpdate() 来将它们存入 Realm。

### RXjava
Realm 包含了对 RxJava 的原生支持。如下类可以被暴露为一个 Observable：Realm, RealmResults, RealmObject, DynamicRealm and DynamicRealmObject。
这些类直接调用asObservable()即可转换成一个Observable对象.

## 调试
当你使用 Android Studio 或者 IntelliJ 调试的时候请留神：调试视图中显示的变量值可能会造成误导。
举个例子，在 Android Studio 中查看一个RealmObject的所有属性。你会发现这些属性与期望不符。这时因为 Realm 为每个 RealmObject 创建了代理类，通过使用代理类的 getter 和 setter 方法来存取数据，从而原始对象的属性不会被赋值。


## 目前的限制
- 字符串排序与查询
- 多线程（Threads）
- Realm 文件不支持多进程访问
- RealmObject’s hashCode

## 最佳实践
### 防止出现 ANR
一般来说 Realm 的读写是足够快的，甚至在 UI 线程中读写也不是问题。但是，写事务是互相阻塞的，所以为了避免 ANR 的出现，我们建议你在后台线程中执行写操作。

### 控制 Realm 实例的生命周期
RealmObjects 和 RealmResults 在访问其引用数据时都是**懒加载**的。因为这个原因，请不要关闭你的 Realm 实例,如果你仍然需要访问其中的 Realm 对象或者查询结果。为了避免不必要的 Realm 数据连接的打开和关闭，Realm 内部有一个基于引用计数的缓存。这表示在同一线程内调用 Realm.getDefaultInstance() 多次是基本没有开销的，并且底层资源会在所有实例都关闭的时候才被释放。

### 重用 RealmResults 和 RealmObjects
在 UI 线程和其它拥有 Looper 的线程中，RealmObject 和 RealmResults 都会在 Realm 数据改变时自动刷新。这意味着你不需要在 RealmChangeListener 中重新获取这些对象。它们已经被更新并且准备好被重绘在屏幕上了。


## Realm 移动端平台 (The Realm Mobile Platform)
### 开启 Realm 移动端平台支持 (Enabling Realm Mobile Platform)
请添加如下代码到你的 app 的 build.gradle 文件中.

```groovy
realm {
  syncEnabled = true;
}
```

### 创建用户并登录 (Creating and Logging in Users)

Realm SyncUser 对象是 Realm 对象服务器的核心组件，它与每个需要同步的 Realm 数据库相关联。SyncUser 对象支持的登录方式包括常规的用户名/密码登录和第三方的登录。

创建用户并登录需要如下两个信息：

    相应的 Realm 登录服务器的 URL；

    相关的校验信息（例如用户名/密码或者访问秘钥等）。
    A URL (as a string) of a Realm Authentication Server to connect to.
    Credentials for an authentication mechanism that describes the user as appropriate for that mechanism (i.e., username/password, access key, etc).

Realm 通过这些信息来创建 SyncUser 对象。

#### 创建校验信息 (Creating a Credential)
























