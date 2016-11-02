package cn.steve.notification;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import steve.cn.mylib.util.SystemUtils;

/**
 * Created by steveyan on 16-10-14.
 */

public class NotificationsUtils {

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT > 18) {
            return highVersionCheck(context);
        } else {
            return lowVersionCheck(context);
        }
    }

    private static boolean lowVersionCheck(Context context) {
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) opPostNotificationValue.get(Integer.class);
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * in this Google I/O 2016 video.this method have to be called after api 19
     */
    private static boolean highVersionCheck(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * go to notification setting to enable notification
     *
     * @param context current app context
     */
    public static void go2Setting(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS"); // this action have been @hide in new api
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        if (SystemUtils.isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            Intent intent2 = new Intent();
            intent2.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent2.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent2);
        }
    }
}
