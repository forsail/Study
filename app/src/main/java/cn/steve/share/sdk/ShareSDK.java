package cn.steve.share.sdk;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;

import cn.steve.share.ShareTo;
import cn.steve.study.R;

import static cn.steve.share.ShareTo.LINK;
import static cn.steve.share.ShareTo.MESSAGE;
import static cn.steve.share.ShareTo.QQ;
import static cn.steve.share.ShareTo.WECHAT;
import static cn.steve.share.ShareTo.WECHAT_FAVOURITE;
import static cn.steve.share.ShareTo.WECHAT_TIMELINE;
import static cn.steve.share.ShareTo.WEIBO;

/**
 * Created by SteveYan on 2017/4/28.
 */

public class ShareSDK {

    private ArrayMap<String, ShareData> datas = new ArrayMap<>();
    private ArrayList<ShareItem> items = new ArrayList<>();


    public void share(ShareData shareData, ShareTo shareTo) {
    }

    void dealOnclick(int shareItem) {
        switch (shareItem) {
            case WECHAT:
                break;
            case WECHAT_TIMELINE:
                break;
            case WECHAT_FAVOURITE:
                break;
            case WEIBO:
                break;
            case QQ:
                break;
            case MESSAGE:
                break;
            case LINK:
                break;
        }
    }

    public void copy() {
        ShareItem copy = new ShareItem(R.drawable.share_copylink, "复制链接");
        copy.setType(ShareTo.LINK);
        items.add(copy);
    }

    public void sms() {
        ShareItem sms = new ShareItem(R.drawable.share_sms, "短信");
        sms.setType(ShareTo.MESSAGE);
        items.add(sms);
    }

    public void sinaWeibo() {
        ShareItem sinaWeibo = new ShareItem(R.drawable.share_sina, "新浪微博");
        sinaWeibo.setType(ShareTo.WEIBO);
        items.add(sinaWeibo);

    }

    public void favourite() {
        ShareItem favourite = new ShareItem(R.drawable.share_wx_favourite, "微信收藏");
        favourite.setType(ShareTo.WECHAT_FAVOURITE);
        items.add(favourite);
    }

    public void timeLine() {
        ShareItem timeTine = new ShareItem(R.drawable.share_wx_timeline, "微信朋友圈");
        timeTine.setType(ShareTo.WECHAT_TIMELINE);
        items.add(timeTine);
    }

    public void weChat() {
        ShareItem weChat = new ShareItem(R.drawable.share_wx_friend, "微信好友");
        weChat.setType(ShareTo.WECHAT);
        items.add(weChat);
    }


    public void qq() {
        ShareItem qq = new ShareItem(R.drawable.share_qq, "QQ");
        qq.setType(ShareTo.QQ);
        items.add(qq);
    }
}
