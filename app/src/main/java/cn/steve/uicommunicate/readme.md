在主线程上另外开辟线程

在子线程中更新 UI 的时候报的异常是

Only the original thread that created a view hierarchy can touch its views.

而不是直接了当地说没有在UI 线程进行更新.

在viewrootimpl 类中,有个  checkThread 方法,检查当前线程是否就是 构造 ViewRootImpl 时候,传递进去的线程.

该代码出自 framework/base/core/java/android/view/ViewRootImpl.java

目前根据我的观察,应该是只要赶在 onWindowFocusChanged 之前就可以.在 onWindowFocusChanged 中,会调用 windowmanager 方法,更新整个布局,



在 ActivityThread 类中的 handleResumeActivity 方法中,会 

```java
if (r.activity.mVisibleFromClient) {
ViewManager wm = a.getWindowManager();
View decor = r.window.getDecorView();
wm.updateViewLayout(decor, l); //这个调用会产生以下一连串的调用,导致了线程检查
}



/////
 r.activity.mVisibleFromServer = true;
                mNumVisibleActivities++;
                if (r.activity.mVisibleFromClient) {
                    r.activity.makeVisible();
                }
```

activity 的 makeVisible 方法

```java
 void makeVisible() {
        if (!mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(mDecor, getWindow().getAttributes());
            mWindowAdded = true;
        }
        mDecor.setVisibility(View.VISIBLE);
    }
```

WindowManager 的 addView 方法由 windowmanagerimpl 实现.

```java
@Override
    public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
        applyDefaultToken(params);
        mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
    }
```

在 WindowManagerGlobal 类的 addView 方法中.

```java
root = new ViewRootImpl(view.getContext(), display);
view.setLayoutParams(wparams);
mViews.add(view);
mRoots.add(root);
mParams.add(wparams);
```

实例化了 ViewRootImpl 对象.同时调用了 setView 方法.
```java
 try {
            root.setView(view, wparams, panelParentView);
        } catch (RuntimeException e) {
            // BadTokenException or InvalidDisplayException, clean up.
            synchronized (mLock) {
                final int index = findViewLocked(view, false);
                if (index >= 0) {
                    removeViewLocked(index, true);
                }
            }
            throw e;
        }
```

细看ViewRootImpl 的方法,里面又调用了 requestLayout 方法.

```java
@Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }
```
这里做了线程检查.

所以,目前位置,可以确定在 onresume 之后可能就会产生线程检查的问题.但是目前我测试还发现在 onresume 里还是ok的,可能还有其他的什么问题,没有被发现.





1. getWindowManager().updateViewLayout(decor, params);实则调用的是 WindowManagerImpl 的 updateViewLayout 方法.

2. 在 WindowManagerImpl 的 updateViewLayout 方法中,又调用了 mGlobal.updateViewLayout(view, params);

3. mGlobal 就是 WindowManagerGlobal,在这个类的 updateViewLayout 方法中,会创建 ViewRootImpl 对象,并且  调用 setLayoutParams 方法,设置布局参数.

4. 在 ViewRootImpl 的 setLayoutParams 方法中,会 调用  方法,开始对view 的测量,布局和绘制.
scheduleTraversals

5. 在 scheduleTraversals 方法中,有一个 mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
这里的 mTraversalRunnable 任务,里面只执行一个方法,doTraversal方法.这个方法中又调用了 performTraversals 方法,正式开启了对view的操作.

6. 



