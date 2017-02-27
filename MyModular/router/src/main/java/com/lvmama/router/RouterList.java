package com.lvmama.router;

import java.util.LinkedHashMap;

/**
 * 记录所有 fragment 和 activity 的全路径
 *
 * Created by yantinggeng on 2016/11/8.
 */

public class RouterList {

    private static LinkedHashMap<String, String> lists = new LinkedHashMap<>();

    public static void inject(String key, String value) {
        lists.put(key, value);
    }

    public static String getActivity(String key) {
        return lists.get(key);
    }

}
