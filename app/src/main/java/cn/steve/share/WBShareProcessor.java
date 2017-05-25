package cn.steve.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.WbAppInfo;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import cn.steve.study.R;

/**
 * Created by SteveYan on 2017/5/25.
 */

public class WBShareProcessor {
    private Activity activity;
    private WbShareHandler shareHandler;
    private boolean clientOnly = false;

    private String strone = "详情猛戳";
    private String strtwo = "下载驴妈妈客户端，旅游出行全部hold住。";

    private String content = "我在@驴妈妈旅游 发现了上海出发+昆明6天+跟团游，只要2578元起！要约的报名走起？";
    private String productURL = "https://m.lvmama.com/product/1080481/f9";
    private String downloadURL = "https://shouji.lvmama.com/?ch=LVMM";
    private Bitmap bitmap;


    public WBShareProcessor(Activity activity) {
        this.activity = activity;
        shareHandler = new WbShareHandler(activity);
        shareHandler.registerApp();
        initParam();
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMessage() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj();
        weiboMessage.imageObject = getImageObj();
        weiboMessage.mediaObject = getWebpageObj();
        shareHandler.shareMessage(weiboMessage, clientOnly);
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = content;
        textObject.title = strone;
        textObject.actionUrl = productURL;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = strtwo;
        mediaObject.description = "驴妈妈描述";
        //  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = downloadURL;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    private void initParam() {
        if (isWbAppInstalled()) {
            clientOnly = true;
            content += strone + productURL;
        } else {
            clientOnly = false;
            content += strone + productURL + strtwo + downloadURL;
        }
        bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.xihu);
    }

    private boolean isWbAppInstalled() {
        WbAppInfo wbAppInfo = WeiboAppManager.getInstance(activity).getWbAppInfo();
        return wbAppInfo != null && wbAppInfo.isLegal();
    }

}
