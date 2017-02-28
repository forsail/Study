package com.lvmama.mymodular;

import android.app.Application;

/**
 * Created by Steve on 2017/2/28.
 */

public class ModularApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppLoader.init();
    }
}
