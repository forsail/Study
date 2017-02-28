package com.lvmama.router;

import java.util.LinkedHashMap;

/**
 * 记录所有 fragment 和 activity 的全路径
 *
 * 采用键值对的方式，key 是Activity的名字，value 是 Activity 的全路径，对于每个 module 必须手动调用加载 RouteTemp
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
