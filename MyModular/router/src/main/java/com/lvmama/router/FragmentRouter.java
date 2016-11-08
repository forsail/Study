package com.lvmama.router;

import android.support.v4.app.Fragment;

/**
 * Created by yantinggeng on 2016/11/8.
 */

public class FragmentRouter {

    public Fragment getInstance(String fragmentName) {
        Fragment fragment;
        try {
            Class fragmentClass = Class.forName(fragmentName);
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fragment;
    }
}
