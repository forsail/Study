# Message

## 概述
在一个以消息驱动的系统中，最重要的两部分就是**消息队列**和**消息处理循环**.

在 Android2.3之前,只有 Java 层有 MessageQueue,之后将 MessageQueue 的核心部分下移至 Native 层.这样就会让整个流程更加复杂.

## MessageQueue

### 创建 MessageQueue
MessageQueue 的创建是由 native 层创建的,

```java
MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
}
```







## Looper
这里负责取数据




