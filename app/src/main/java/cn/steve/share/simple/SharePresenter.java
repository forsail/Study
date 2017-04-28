package cn.steve.share.simple;

import android.content.Context;

import cn.steve.share.sdk.ISharePresenter;

/**
 * Created by steveyan on 16-10-4.
 */

public class SharePresenter extends ISharePresenter {

    private Context context;

    public SharePresenter(Context context) {
        this.context = context;
    }


    @Override
    public void shareByQQ() {

    }

    @Override
    public void shareByWechat() {

    }

    @Override
    public void shareByWechatTimeLine() {

    }

    @Override
    public void shareByWechatFavourite() {

    }

    @Override
    public void shareByWeibo() {

    }

    @Override
    public void shareByMessage() {

    }

    @Override
    public void shareByLink() {

    }
}
