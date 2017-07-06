# 传统交互模式
## Android
### Native 调 JS

native调用js比较简单，只要遵循："JavaScript: 方法名(‘参数,需要转为字符串’)" 的规则即可。

在4.4之前，调用的方式：

```java
mWebView.loadUrl("javascript: 方法名('参数,需要转为字符串')"); 

```
4.4以后(包括4.4)，使用以下方式：

```java
mWebView.evaluateJavascript("javascript: 方法名('参数,需要转为字符串')", new ValueCallback() {
        @Override
        public void onReceiveValue(String value) {
            //这里的value即为对应JS方法的返回值
        }
});
```

**小结**
- 4.4之前Native通过loadUrl来调用JS方法,只能让某个JS方法执行,但是无法获取该方法的返回值
- 4.4之后,通过evaluateJavascript异步调用JS方法,并且能在onReceiveValue中拿到返回值
- 不适合传输大量数据(大量数据建议用接口方式获取),mWebView.loadUrl(“javascript: 方法名(‘参数,需要转为字符串’)”);
- 函数需在UI线程运行，因为mWebView为UI控件


### JS调Native
Js调用Native需要对WebView设置@JavascriptInterface注解.要想js能够Native，需要对WebView设置以下属性。
```java
WebSettings webSettings = mWebView.getSettings();
// Android 容器允许 JS 脚本
webSettings.setJavaScriptEnabled(true);
// Android 容器设置桥接对象，JS 就可以通过这个对象调用 native 暴露的方法
mWebView.addJavascriptInterface(getJSBridge(), "JSBridge");
```

```java
public class JSBridgeObject {
      @JavascriptInterface
        public String foo(){  
            return "foo";  
        }  

        @JavascriptInterface
        public String foo2(final String param){  
            return "foo2:" + param;  
        }  
}
```

native 端做了准备之后，就是HTML的调用。

```js
window.JSBridge.foo();
window.JSBridge.foo2('test');
```



