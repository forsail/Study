---
title: IPC_Binder_java_2
date: 2017-07-04 14:47:55
tags: [IPC,Binder]
---
# 背景

在开发中，经常需要通过 getSystemService 的方式获取一个系统服务,那么这些系统服务的 Binder 引用是如何传递给客户端的呢？要知道，系统服务并不是通过 startService() 启动的。

# ServiceManager 管理的服务

ServiceManager 是一个独立进程，其作用如名称所示，管理各种系统服务.

ServiceManager 本身也是一个 Service ，Framework 提供了一个系统函数，可以获取该 Service 对应的 Binder 引用，那就是 BinderInternal.getContextObject().该静态函数返回 ServiceManager 后，就可以通过 ServiceManager 提供的方法获取其他系统 Service 的 Binder 引用。这种涉及模式在日常中是可见的，
ServiceManager 就像一个公司的总机，这个号码是公开的，系统中任何进程都可以使用 BinderInternal.getContextObject() 获取该总机的 Binder 对象，而当用户想联系公司中的其他任何人(服务),则要警告总机再获得分机号。这种设计的好处是系统中仅暴露了一个全局 Binder 引用 (ServiceManager),而其他系统服务则可以隐藏起来，从而有助于系统服务的扩展，以及调用系统服务的安全检查。其他系统服务在启动时，首先把自己的 Binder 对象传递给 ServiceManager,即所谓的注册(addService).


举个具体的例子:

- ContextImpl.java
```java
 @Override
    public Object getSystemService(String name) {
        if (WINDOW_SERVICE.equals(name)) {
            return WindowManagerImpl.getDefault();
        } else if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            synchronized (mSync) {
                LayoutInflater inflater = mLayoutInflater;
                if (inflater != null) {
                    return inflater;
                }
                mLayoutInflater = inflater =
                    PolicyManager.makeNewLayoutInflater(getOuterContext());
                return inflater;
            }
        } else if (ACTIVITY_SERVICE.equals(name)) {
            return getActivityManager();
        } else if (INPUT_METHOD_SERVICE.equals(name)) {
            return InputMethodManager.getInstance(this);
        } else if (ALARM_SERVICE.equals(name)) {
            return getAlarmManager();
        } else if (ACCOUNT_SERVICE.equals(name)) {
            return getAccountManager();
        } else if (POWER_SERVICE.equals(name)) {
            return getPowerManager();
        } else if (CONNECTIVITY_SERVICE.equals(name)) {
            return getConnectivityManager();
        } else if (THROTTLE_SERVICE.equals(name)) {
            return getThrottleManager();
        } else if (WIFI_SERVICE.equals(name)) {
            return getWifiManager();
        } else if (NOTIFICATION_SERVICE.equals(name)) {
            return getNotificationManager();
        } else if (KEYGUARD_SERVICE.equals(name)) {
            return new KeyguardManager();
        } else if (ACCESSIBILITY_SERVICE.equals(name)) {
            return AccessibilityManager.getInstance(this);
        } else if (LOCATION_SERVICE.equals(name)) {
            return getLocationManager();
        } else if (SEARCH_SERVICE.equals(name)) {
            return getSearchManager();
        } else if (SENSOR_SERVICE.equals(name)) {
            return getSensorManager();
        } else if (STORAGE_SERVICE.equals(name)) {
            return getStorageManager();
        } else if (VIBRATOR_SERVICE.equals(name)) {
            return getVibrator();
        } else if (STATUS_BAR_SERVICE.equals(name)) {
            synchronized (mSync) {
                if (mStatusBarManager == null) {
                    mStatusBarManager = new StatusBarManager(getOuterContext());
                }
                return mStatusBarManager;
            }
        } else if (AUDIO_SERVICE.equals(name)) {
            return getAudioManager();
        } else if (TELEPHONY_SERVICE.equals(name)) {
            return getTelephonyManager();
        } else if (CLIPBOARD_SERVICE.equals(name)) {
            return getClipboardManager();
        } else if (WALLPAPER_SERVICE.equals(name)) {
            return getWallpaperManager();
        } else if (DROPBOX_SERVICE.equals(name)) {
            return getDropBoxManager();
        } else if (DEVICE_POLICY_SERVICE.equals(name)) {
            return getDevicePolicyManager();
        } else if (UI_MODE_SERVICE.equals(name)) {
            return getUiModeManager();
        }

        return null;
    }
```

- InputMethodManager.java

```java
   /**
     * Retrieve the global InputMethodManager instance, creating it if it
     * doesn't already exist.
     * @hide
     */
    static public InputMethodManager getInstance(Context context) {
        return getInstance(context.getMainLooper());
    }
    
    /**
     * Internally, the input method manager can't be context-dependent, so
     * we have this here for the places that need it.
     * @hide
     */
    static public InputMethodManager getInstance(Looper mainLooper) {
        synchronized (mInstanceSync) {
            if (mInstance != null) {
                return mInstance;
            }
            IBinder b = ServiceManager.getService(Context.INPUT_METHOD_SERVICE);
            IInputMethodManager service = IInputMethodManager.Stub.asInterface(b);
            mInstance = new InputMethodManager(service, mainLooper);
        }
        return mInstance;
    }
```

- ServiceManager.java

```java
    /**
     * Returns a reference to a service with the given name.
     * 
     * @param name the name of the service to get
     * @return a reference to the service, or <code>null</code> if the service doesn't exist
     */
    public static IBinder getService(String name) {
        try {
            IBinder service = sCache.get(name);
            if (service != null) {
                return service;
            } else {
                return getIServiceManager().getService(name);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "error in getService", e);
        }
        return null;
    }

```

通过 ServiceManager 获取 InputMethod Service 对应的 Binder 对象 b,然后再将该 Binder 对象作为 IInputMethodManager.Stub.asInterface 的参数，返回一个 IInputMethodManager 的统一接口。

ServiceManager 的 getService 方法，首先从 sCache 缓存中查看是否有对应的 Binder 对象，有则返回，没有则调用 getIServiceManager().getService().

getIServiceManager()用于返回系统中唯一的 ServiceManager 对应的 Binder。

对应的代码:

```java
private static IServiceManager getIServiceManager() {
        if (sServiceManager != null) {
            return sServiceManager;
        }
        // Find the service manager
        sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
        return sServiceManager;
    }
```

BinderInternal.getContextObject() 方法就是用于获取 ServiceManager 对应的全局 Binder  对象。

至于关于 注册 服务，等到分析 Framework 启动过程的时候描述。


## 理解 Manager

ServiceManager 所管理的所有 Service 都是以相应的 Manager 返回给客户端，所以简述下 Framework 层对于 Manager 的定义。在Android 中，Manager 的含义类似于现实生活中的经纪人，Manager 所 manage 的对象是服务本身，因为每个具体的服务一般都会提供多个 API 接口，而 manager 所 manage 的正式这些 API。客户端一般不能直接通过  Binder  引用去访问具体的服务，而是要警告一个 Manager，相应的 Manager 类对客户端是可见的，而远程的服务端对客户端来说则是隐藏的。

而这些 Manager 的类内部都会有一个远程服务 Binder 的变量，而且在一般情况下，这些 Manager 的构造函数参数中都会包含这个 Binder  对象。简单讲，即先通过 ServiceManager 获取远程服务的 Binder 引用，然后使用这个 Binder 引用构造一个客户端本地可以访问的经纪人，然后客户端就可以通过该经纪人访问远程的服务了。


这种设计的作用是屏蔽了直接访问远程服务，从而给应用程序提供灵活的，可控的 API 接口，比如 AMS ，吸引不希望用户直接访问 AMS，而是经过 ActivityManager 类去访问，而 ActivityManager 内部提供了一些更具可操作性的数据结构化，不如 RecentTaskInfo 数据类封装了最近访问过的 Task 列表；MemoryInfo  数据类封装了和内存相关的信息。



# 感激,非常感激，万分的感激！
* [柯元旦](https://book.douban.com/subject/6811238/)