# Annotation

## Annotation 分类

1. 标准 Annotation
   包括 Override, Deprecated, SuppressWarnings，是java自带的几个注解，他们由编译器来识别，不会进行编译，不影响代码运行。
2. 元 Annotation
   @Retention, @Target, @Inherited, @Documented，它们是用来定义 Annotation 的 Annotation。也就是当我们要自定义注解时，需要使用它们。
3. 自定义 Annotation
   自定义的Annotation。

### 自定义的注解也分为三类，通过元Annotation - @Retention 定义：

>* @Retention(RetentionPolicy.SOURCE)

    源码时注解，一般用来作为编译器标记。如Override, Deprecated, SuppressWarnings。

>* @Retention(RetentionPolicy.RUNTIME)

    运行时注解，在运行时通过反射去识别的注解，这种注解最大的缺点就是反射消耗性能。

>* @Retention(RetentionPolicy.CLASS)

    编译时注解，在编译时被识别并处理的注解，相当于自动生成代码，没有反射，和正常的手写代码无二。

## Annotation 的工作原理

APT(Annotation Processing Tool)



## 自定义注解

前提：自定义注解一定要是 Java library，不能用 Android library。
IDE： AS 新建一个 Android Project
插件：此外我们还需要另外一个库，这个库是为了在 Android 上只用注解而使用的: android-apt。 这个插件可以自动的帮你为生成的代码创建目录, 让生成的代码编译到APK里面去, 而且它还可以让最终编译出来的APK里面不包含注解处理器本身的代码。

- 允许配置只在编译时作为注解处理器的依赖，而不添加到最后的APK或library
- 设置源路径，使注解处理器生成的代码能被Android Studio正确的引用


### 编译时注解

对于编译时注解，在编译项目之前执行的代码，可以生成代码或者生成其他文件，生成的文件将会被打包进项目，但是之前的注解将会被删除，不会进入 class 文件。

#### 传统方式

1.  新建一个 Java module，设置该 module 的 gradle 文件
```groovy
apply plugin: 'java'
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
sourceCompatibility = "1.7"
targetCompatibility = "1.7"
```
同时设置 project 的 gradle
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.2.2'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}
```

2.  新建一个注解 NameGenerate

```java
@Retention(RetentionPolicy.CLASS)
public @interface NameGenerate {
}
```

3. 接下来就是重点了，需要编写注解的处理器

```java

public class NameGenerateProcessor extends AbstractProcessor {

    public static final String CLASSNAME = "NameGeneateList";
    public static final String PACKAGENAME = "com.lvmama.router";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Messager messager = processingEnv.getMessager();

        try {
        	// 以新建文件的文件名为参数新建一个 JavaFileObject 对象
            JavaFileObject f = processingEnv.getFiler().createSourceFile(CLASSNAME);
            Writer w = f.openWriter();
            PrintWriter pw = new PrintWriter(w);
			// 将新建文件的内容就以拼接的方式输入即可。
            pw.println("package " + PACKAGENAME + ";");
            pw.println("\npublic class " + CLASSNAME + " { ");

            for (Element element : env.getElementsAnnotatedWith(NameGenerate.class)) {
                PackageElement packageElement = (PackageElement) element.getEnclosingElement();
                String packageName = packageElement.getQualifiedName().toString();
                TypeElement classElement = (TypeElement) element;
                String className = classElement.getSimpleName().toString();
                String fullClassName = classElement.getQualifiedName().toString();
                pw.print("public String " + className + "=\"" + fullClassName + "\";");
            }

            pw.println("}");
            pw.flush();
            pw.close();

        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NameGenerate.class.getCanonicalName());
        return types;
    }
}
```

4. 注册处理器
在src/main目录下，新建一个和 Java  文件夹平级的文件夹 "resources", 在 resources 文件夹下新建 META-INF 文件夹，在 META-INF 文件夹下新建 services 文件夹， 在 services 文件夹下新建 javax.annotation.processing.Processor 文件，文件夹的内容就是刚刚编写的处理的全路径名，例如 com.steve.NameGenerateProcessor

5. 使用
有两种方式，一是直接在 gradle 中依赖 注解项目。
```groovy
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile project(':lib_processor')
}
```
另外一种方式就是直接将注解的项目打出 jar 文件，让 app 依赖这个 jar  文件。

build app 项目，就会生成对应的文件。文件的路径:\app\build\generated\source\apt\debug
在代码中直接引用即可。

#### 借助 Google 和 square 的库
传统的方式，过程比较繁琐，借助 Google  的 auto-service 和 square 的 javapoet 可以省很多事。

- Auto
用来注解 Processor 类，生成对应的 META-INF 的配置信息，省去注册处理器这一步，只要在自定义的 Processor 上面加上 @AutoService(Processor.class)
- javapoet
只是一个方便生成代码的一个库，比起简单的字符串拼接，这个看上去更加友好。



### 方法解释

- Element:
表示一个程序元素，比如包、类或者方法。每个元素都表示一个静态的语言级构造（不表示虚拟机的运行时构造）。 元素应该使用 equals(Object)方法进行比较。不保证总是使用相同的对象表示某个特定的元素。要实现基于Element对象类的操作，可以使用 visitor 或者使用 getKind() 方法的结果。使用 instanceof 确定此建模层次结构中某一对象的有效类未必可靠，因为一个实现可以选择让单个对象实现多个 Element 子接口。

- TypeElement ：
TypeElement 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注释类型是一种接口.
TypeElement 代表了一个 class 或者 interface 的 element 。DeclaredType 表示一个类或接口类型，后者(DeclaredType)将成为前者(TypeElement)的一种使用（或调用）。这种区别对于一般的类型是最明显的，对于这些类型，单个元素可以定义一系列完整的类型。 例如，元素 java.util.Set 对应于参数化类型 java.util.Set<String> 和 java.util.Set<Number>（以及其他许多类型），还对应于原始类型 java.util.Set。





### 对于注解循环的解释
此处解释引用自《深入Java虚拟机_JVM高级特性与最佳实践》P307。
将插入式注解处理器看做一个插件，如果这些插件在处理注解期间对语法树进行了修改，编译器将回到解析及填充符号表的过程重新处理，直到所有插入式注解处理器都没有再对语法树进行修改为止，每一次的循环称为一个round。