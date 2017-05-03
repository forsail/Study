package cn.steve.share.sdk;


import cn.steve.share.sdk.ui.ShareItem;

/**
 * Created by steveyan on 16-10-4.
 */

public abstract class ISharePresenter {

    public void dealOnclick(ShareItem shareItem) {
        shareItem.getAction().share();
    }
}
