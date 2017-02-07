# Why
本文是一篇译文，是我在阅读一篇文章时候顺带看到的。内容是官方的文档，链接见末位参考部分。
#  Build Cache
## Introduction
在 *Android Studio 2.2 Beta3* 中介绍了一种可以减少编译时间的新 *build cache*  缓存特性，这个新特性可以加快包括全量编译，增量编译和 instant run 的编译时间，通过保存和复用前一次由同一个项目或者其他项目 build 产生的文件或者文件夹。
*build cache* 目的是为了在所有的 Android 项目中共用。开发者可以通过修改 *gradle.properties* 文件，实现是否启用 *build cache* 和指定缓存的位置。当前 *build cache*  只包含 *pre-dexed* 库，未来，*Android studio* 团队会支持其他类型的文件。

**注意**：*build cache*  的实现是和  *gradle cache*  管理（例如,reporting up-to-date statuses）是相互独立的。当执行一个 task 的时候，无论是否使用 *build cache*  对于 Gradle 而言都是未知的（即：即使命中了缓存，Gradle 也不会认为是 up-to-date）。然而，当使用 *build cache* 的时候，还是希望加快编译速度的。
即使目前还未发现有任何问题，我们希望给社区更多的时间以提供更多的反馈。目前这个特性仍旧作为实验性的特性，目前默认还是禁用的。（Android Studio 2.3 Canary 1 开始默认启用）。根据未来的反馈情况，当我们觉得这个特性稳定了，将会在 Android Studio 2.3 或者 2.4 中默认启动。

## How to use the Build Cache
### Step 0
确保 [android.dexOptions.preDexLibraries](http://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.DexOptions.html#com.android.build.gradle.internal.dsl.DexOptions:preDexLibraries)已经设置为 **true**。否则 *libraries* 不会被 *pre-dexed*，因而 *build cache* 并不会被使用。

### Step 1
在 Android 项目中打开 *gradle.properties*，添加以下两个参数
```groovy
android.enableBuildCache=true # true:启用 build cache，反之禁用。如果这个参数未设置，默认是禁用 build cache.

android.buildCacheDir=<path-to-build-cache-directory> # 这个是个可选项，用来指定 build cache 目录的绝对路径。如果设置成项目路径，那么是项目于项目的根目录而言的。如果这个参数未被设置，那么默认的目录是 <user-home-directory>/.android/build-cache。如果使用相同的缓存目录，那么多个项目可以共用相同的缓存，所以，推荐使用默认的路径或者使用一个项目外的绝对路径。任何情况下，build cache 的路径都不应该放在 "build" 文件夹下，除非每次运行 clean 之后，都能删除 build cache 。如果 android.enableBuildCache 被设置成 false，则这个参数将会被忽略。

```

### Step 2
build 项目，或者在命令行下执行 *./gradlew assemble*,检查以下位置，查看 build cache 是否起作用。
- 缓存的文件被存储在了上述  android.buildCacheDir 指定的文件夹下。默认情况下，是在 *<user-home-directory>/.android/build-cache.*
- 最终的 pre-dexed 文件被存储在了 *<project-dir/module-dir>/build/intermediates/pre-dexed/debug* 和 *<project-dir/module-dir>/build/intermediates/pre-dexed/release.*。可以在命令行下运行指令查看  “pre-dexed” 文件夹。如果点击的是 Android Studio 面板上的 “Run”  按钮，时无法看到这个文件夹的，因为这个文件夹背会被删除。

**注意**:
如果使用 Multi-dex 并且 minSdk >= 21 ，那么 dexed files 将会被直接保存在 *<project-dir/module-dir>/build/intermediates/transforms/dex* 目录下， 而不是在 *<project-dir/module-dir>/build/intermediates/pre-dexed*.

** Cleaning the Build Cache
如果想要清除 *build cache*， 可以直接删除 *build cache* 文件夹内的内容。
*build cache* 文件夹在 *android.buildCacheDir* 指定的目录下,或者在默认的 *<user-home-directory>/.android/build-cache* 文件夹下.

从 Android Studio 2.3 Canary 1 开始，Gradle task 中新增了一个叫做 *cleanBuildCache* 的任务，可以更加便利的删除 *build cache* 。
```shell
./gradlew cleanBuildCache
```

# Reference
1. (Build Cache)[http://tools.android.com/tech-docs/build-cache]
