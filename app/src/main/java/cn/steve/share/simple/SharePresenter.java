package cn.steve.share.simple;

import android.content.Context;

import cn.steve.share.sdk.ISharePresenter;

/**
 * 各个分享行为的具体实现
 *
 * Created by steveyan on 16-10-4.
 */

public class SharePresenter extends ISharePresenter {

    private Context context;

    public SharePresenter(Context context) {
        this.context = context;
    }

}
