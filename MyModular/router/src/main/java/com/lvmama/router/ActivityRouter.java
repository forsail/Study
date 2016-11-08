package com.lvmama.router;

import android.content.Context;
import android.content.Intent;

/**
 * Created by yantinggeng on 2016/11/8.
 */

public class ActivityRouter {

    public static void startActivityForName(Context context, String name) {

        try {
            Class cla = Class.forName(name);
            context.startActivity(new Intent(context, cla));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
