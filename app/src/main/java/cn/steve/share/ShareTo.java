package cn.steve.share;

import java.util.ArrayList;

/**
 * 为保持统一，这个放到公用处
 *
 * Created by SteveYan on 2017/4/28.
 */

public class ShareTo {

    public static final int WECHAT = 11;
    public static final int WECHAT_TIMELINE = 22;
    public static final int WECHAT_FAVOURITE = 33;
    public static final int WEIBO = 44;
    public static final int QQ = 55;
    public static final int MESSAGE = 66;
    public static final int LINK = 77;
    public static final int ALL = 88;
    private ArrayList<Integer> targets = new ArrayList<>();

    public ArrayList<Integer> getTargets() {
        return targets;
    }

    public void addTarget(int target) {
        targets.add(target);
    }


}
