package cn.steve.share.sdk.ui;

import android.support.annotation.DrawableRes;

import cn.steve.share.sdk.ShareAction;
import cn.steve.share.sdk.ShareCallBack;
import cn.steve.share.sdk.ShareData;

/**
 * Created by yantinggeng on 2016/5/5.
 */
public class ShareItem {

    @DrawableRes
    private int drawableRes;
    private int type;
    private String text;

    private ShareAction action;
    private ShareData shareData;
    private ShareCallBack shareCallBack;

    public ShareItem(@DrawableRes int drawableRes, String text) {
        this.drawableRes = drawableRes;
        this.text = text;
    }

    public ShareCallBack getShareCallBack() {
        return shareCallBack;
    }

    public ShareItem setShareCallBack(ShareCallBack shareCallBack) {
        this.shareCallBack = shareCallBack;
        return this;
    }

    public ShareData getShareData() {
        return shareData;
    }

    public void setShareData(ShareData shareData) {
        this.shareData = shareData;
    }

    public ShareAction getAction() {
        return action;
    }

    public void setAction(ShareAction action) {
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public String getText() {
        return text;
    }

}
