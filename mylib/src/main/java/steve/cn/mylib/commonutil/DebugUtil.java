package steve.cn.mylib.commonutil;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by Steve on 2017/3/13.
 */

public class DebugUtil {

    private static Boolean debug = null;

    public static boolean isDebug() {
        return debug == null ? false : debug;
    }

    public static void syncDebug(Context context) {
        if (debug == null) {
            debug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }


}
