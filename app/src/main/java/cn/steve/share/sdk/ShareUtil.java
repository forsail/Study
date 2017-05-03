package cn.steve.share.sdk;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import cn.steve.share.ShareConstant;
import cn.steve.share.sdk.ui.BottomSheetShareFragment;
import cn.steve.share.sdk.ui.ShareItem;
import cn.steve.study.R;

/**
 * 根据调用传入的配置和数据生成分享界面
 *
 * Created by steveyan on 16-10-4.
 */

public class ShareUtil {

    private AppCompatActivity context;
    private ArrayList<ShareItem> items = new ArrayList<>();

    public ShareUtil(AppCompatActivity context) {
        this.context = context;
    }

    /**
     * 根据配置生成 ShareItems
     *
     * @param config 配置文件
     */
    public void generate(ShareSDK config) {
        for (Integer integer : config.getShareTos()) {
            switch (integer) {
                case ShareConstant.ALL:
                    break;
                case ShareConstant.QQ:
                    items.add(qq(config));
                    break;
                case ShareConstant.WECHAT:
                    items.add(weChat(config));
                    break;
                case ShareConstant.WECHAT_FAVOURITE:
                    items.add(favourite(config));
                    break;
                case ShareConstant.WECHAT_TIMELINE:
                    items.add(timeLine(config));
                    break;
                case ShareConstant.WEIBO:
                    items.add(sinaWeibo(config));
                    break;
                case ShareConstant.MESSAGE:
                    items.add(sms(config));
                    break;
                case ShareConstant.LINK:
                    items.add(copyLink(config));
                    break;
            }
        }
        BottomSheetShareFragment fragment = new BottomSheetShareFragment(items);
        fragment.show(context.getSupportFragmentManager(), "shareFragment");
    }


    private ShareItem copyLink(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_copylink, "复制链接");
        item.setType(ShareConstant.LINK);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getCopyTitle())
            .setContent(config.getCopyContent())
            .setImageUrl(config.getCopyImageUrl())
            .setPicPath(config.getCopyPicPath())
            .setProductUrl(config.getCopyProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getCopyShareCallBack());
        item.setAction(new CopyLinkAction(context));
        return item;
    }

    private ShareItem sms(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_sms, "短信");
        item.setType(ShareConstant.MESSAGE);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getSmsTitle())
            .setContent(config.getSmsContent())
            .setImageUrl(config.getSmsImageUrl())
            .setPicPath(config.getSmsPicPath())
            .setProductUrl(config.getSmsProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getSmsShareCallBack());
        item.setAction(new SMSAction(context));
        return item;
    }

    private ShareItem sinaWeibo(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_sina, "新浪微博");
        item.setType(ShareConstant.WEIBO);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getWeiboTitle())
            .setContent(config.getWeiboContent())
            .setImageUrl(config.getWeiboImageUrl())
            .setPicPath(config.getWeiboPicPath())
            .setProductUrl(config.getWeiboProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getWeiboShareCallBack());
        item.setAction(new WeiboAction(context));
        return item;
    }

    private ShareItem favourite(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_wx_favourite, "微信收藏");
        item.setType(ShareConstant.WECHAT_FAVOURITE);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getWxFavoriteTitle())
            .setContent(config.getWxFavoriteContent())
            .setImageUrl(config.getWxFavoriteImageUrl())
            .setPicPath(config.getWxFavoritePicPath())
            .setProductUrl(config.getWxFavoriteProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getWxShareCallBack());
        item.setAction(new WXAction(context));
        return item;
    }

    private ShareItem timeLine(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_wx_timeline, "微信朋友圈");
        item.setType(ShareConstant.WECHAT_TIMELINE);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getWxTimeLineTitle())
            .setContent(config.getWxTimeLineContent())
            .setImageUrl(config.getWxTimeLineImageUrl())
            .setPicPath(config.getWxTimeLinePicPath())
            .setProductUrl(config.getWxTimeLineProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getWxShareCallBack());
        item.setAction(new WXAction(context));
        return item;
    }

    private ShareItem weChat(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_wx_friend, "微信好友");
        item.setType(ShareConstant.WECHAT);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getWxSessionTitle())
            .setContent(config.getWxSessionContent())
            .setImageUrl(config.getWxSessionImageUrl())
            .setPicPath(config.getWxSessionPicPath())
            .setProductUrl(config.getWxSessionProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getWxShareCallBack());
        item.setAction(new WXAction(context));
        return item;
    }


    private ShareItem qq(ShareSDK config) {
        ShareItem item = new ShareItem(R.drawable.share_qq, "QQ");
        item.setType(ShareConstant.QQ);
        ShareData shareData = new ShareData.ShareDataBuilder()
            .setTitle(config.getQqTitle())
            .setContent(config.getQqContent())
            .setImageUrl(config.getQqImageUrl())
            .setPicPath(config.getQqPicPath())
            .setProductUrl(config.getQqProductUrl())
            .build();
        item.setShareData(shareData);
        item.setShareCallBack(config.getQqShareCallBack());
        item.setAction(new QQAction(context));
        return item;
    }
}
