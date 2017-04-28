package cn.steve.share.sdk;


import static cn.steve.share.ShareTo.LINK;
import static cn.steve.share.ShareTo.MESSAGE;
import static cn.steve.share.ShareTo.QQ;
import static cn.steve.share.ShareTo.WECHAT;
import static cn.steve.share.ShareTo.WECHAT_FAVOURITE;
import static cn.steve.share.ShareTo.WECHAT_TIMELINE;
import static cn.steve.share.ShareTo.WEIBO;

/**
 * Created by steveyan on 16-10-4.
 */

public abstract class ISharePresenter {

    void dealOnclick(ShareItem shareItem) {
        switch (shareItem.getType()) {
            case WECHAT:
                shareByWechat();
                break;
            case WECHAT_TIMELINE:
                shareByWechatTimeLine();
                break;
            case WECHAT_FAVOURITE:
                shareByWechatFavourite();
                break;
            case WEIBO:
                shareByWeibo();
                break;
            case QQ:
                shareByQQ();
                break;
            case MESSAGE:
                shareByMessage();
                break;
            case LINK:
                shareByLink();
                break;
        }
    }

    public abstract void shareByQQ();

    public abstract void shareByWechat();

    public abstract void shareByWechatTimeLine();

    public abstract void shareByWechatFavourite();

    public abstract void shareByWeibo();

    public abstract void shareByMessage();

    public abstract void shareByLink();
}
