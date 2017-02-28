package com.lvmama.mymodular;

import com.lvmama.module_login.LoginLoader;
import com.lvmama.module_mine.MineLoader;

/**
 * Created by Steve on 2017/2/28.
 */

public class AppLoader {

    public static void init() {
        LoginLoader.init();
        MineLoader.init();
    }
}
