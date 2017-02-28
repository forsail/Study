##附：组件化需要解决的问题

- module 中的 application 调用问题
如果每个 module 有自己的 application，并且 application 中有自己的实现，那么在 merge 的时候就会出现强转错误的问题。

- 跨 module 中的 activity 和 fragment 的跳转问题。
 这个可以采用隐式跳转，或 Scheme 跳转，这里用的是类名跳转。借助反射的方式。

- 资源命名冲突的问题。
在各个module中的资源文件的名字可能一样，就会出现冲突的可能。

- 重复依赖的问题。

```groovy
App ->Module B
App ->Module A-> Module B
```

这就造成了重复依赖，M 会被重复导入，打包进入最终的代码中。

这种重复依赖的问题，在AAR上是没问题的，会自动取最新的版本的代码。

