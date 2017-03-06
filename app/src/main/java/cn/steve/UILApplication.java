package cn.steve;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends MultiDexApplication {

    //init the image loader params
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
            .name("MyRealm.realm")
            .deleteRealmIfMigrationNeeded()
            .build();
        Realm.setDefaultConfiguration(configuration);
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate() {
        printProgressInfo();
        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        setActivityLifeCycleListener();
        super.onCreate();
        //防止多进程程序多次启动application导致资源浪费现象
        String processName = steve.cn.mylib.util.SystemUtils.getProcessName(this, android.os.Process.myPid());
        String processName2 = steve.cn.mylib.util.SystemUtils.getProcessName();
        Log.e("UILApplication", "processName:" + processName);
        Log.e("UILApplication", "processName2:" + processName2);
        if (processName2 != null) {
            boolean defaultProcess = processName2.equals("cn.steve.study");
            if (defaultProcess) {
                Log.e("UILApplication", "默认的初始化");
                initImageLoader(getApplicationContext());
                initRealm();
                //initBaiduMap();
                initStetho();
            }
        }
    }

    //注册activity的声明周期回调
    private void setActivityLifeCycleListener() {

        // 事件监听回调 ps:这边似乎没有效果
        Foreground.ForeBackStateChangedListener
            myListener =
            new Foreground.ForeBackStateChangedListener() {
                @Override
                public void onBecameForeground() {
                    System.out.println("App 到达前台了");
                }

                @Override
                public void onBecameBackground() {
                    System.out.println("App 切换到了后台");
                }
            };

        //添加activity的生命周期回调
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Foreground.init(this);
            Foreground.listener = myListener;
        }
    }

    //print the progress info
    private void printProgressInfo() {
        Log.i("SteveApplication", Runtime.getRuntime().toString());
    }

    private void initBaiduMap() {
        SDKInitializer.initialize(getApplicationContext());
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

}