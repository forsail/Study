package cn.steve.share.sdk;

import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

import cn.steve.share.ShareConstant;

/**
 * 分享对外提供的 API
 *
 * Created by SteveYan on 2017/5/3.
 */

public class ShareSDK {

    private AppCompatActivity activity;
    private Set<Integer> shareTos = new HashSet<>();
    // 通用部分
    private String title;
    private String content;
    private String imageUrl;
    private String picPath;
    private String productUrl;

    private String wxSessionTitle;
    private String wxSessionContent;
    private String wxSessionImageUrl;
    private String wxSessionPicPath;
    private String wxSessionProductUrl;

    private String wxTimeLineTitle;
    private String wxTimeLineContent;
    private String wxTimeLineImageUrl;
    private String wxTimeLinePicPath;
    private String wxTimeLineProductUrl;

    private String wxFavoriteTitle;
    private String wxFavoriteContent;
    private String wxFavoriteImageUrl;
    private String wxFavoritePicPath;
    private String wxFavoriteProductUrl;

    private String weiboTitle;
    private String weiboContent;
    private String weiboImageUrl;
    private String weiboPicPath;
    private String weiboProductUrl;

    private String qqTitle;
    private String qqContent;
    private String qqImageUrl;
    private String qqPicPath;
    private String qqProductUrl;

    private String copyTitle;
    private String copyContent;
    private String copyImageUrl;
    private String copyPicPath;
    private String copyProductUrl;

    private String smsTitle;
    private String smsContent;
    private String smsImageUrl;
    private String smsPicPath;
    private String smsProductUrl;

    private ShareCallBack shareCallBack;
    private ShareCallBack wxShareCallBack;
    private ShareCallBack weiboShareCallBack;
    private ShareCallBack qqShareCallBack;
    private ShareCallBack copyShareCallBack;
    private ShareCallBack smsShareCallBack;

    public ShareSDK(AppCompatActivity activity) {
        this.activity = activity;
    }

    public String getWxTimeLineTitle() {
        return wxTimeLineTitle == null ? getWxSessionTitle() : wxTimeLineTitle;
    }

    public ShareSDK setWxTimeLineTitle(String wxTimeLineTitle) {
        this.wxTimeLineTitle = wxTimeLineTitle;
        return this;
    }

    public String getWxTimeLineContent() {
        return wxTimeLineContent == null ? getWxSessionContent() : wxTimeLineContent;
    }

    public ShareSDK setWxTimeLineContent(String wxTimeLineContent) {
        this.wxTimeLineContent = wxTimeLineContent;
        return this;
    }

    public String getWxTimeLineImageUrl() {
        return wxTimeLineImageUrl == null ? getWxSessionImageUrl() : wxTimeLineImageUrl;
    }

    public ShareSDK setWxTimeLineImageUrl(String wxTimeLineImageUrl) {
        this.wxTimeLineImageUrl = wxTimeLineImageUrl;
        return this;
    }

    public String getWxTimeLinePicPath() {
        return wxTimeLinePicPath == null ? getWxSessionPicPath() : wxTimeLinePicPath;
    }

    public ShareSDK setWxTimeLinePicPath(String wxTimeLinePicPath) {
        this.wxTimeLinePicPath = wxTimeLinePicPath;
        return this;
    }

    public String getWxTimeLineProductUrl() {
        return wxTimeLineProductUrl == null ? getWxSessionProductUrl() : wxTimeLineProductUrl;
    }

    public ShareSDK setWxTimeLineProductUrl(String wxTimeLineProductUrl) {
        this.wxTimeLineProductUrl = wxTimeLineProductUrl;
        return this;
    }

    public String getWxFavoriteTitle() {
        return wxFavoriteTitle == null ? getWxSessionTitle() : wxFavoriteTitle;
    }

    public ShareSDK setWxFavoriteTitle(String wxFavoriteTitle) {
        this.wxFavoriteTitle = wxFavoriteTitle;
        return this;
    }

    public String getWxFavoriteContent() {
        return wxFavoriteContent == null ? getWxSessionContent() : wxFavoriteContent;
    }

    public ShareSDK setWxFavoriteContent(String wxFavoriteContent) {
        this.wxFavoriteContent = wxFavoriteContent;
        return this;
    }

    public String getWxFavoriteImageUrl() {
        return wxFavoriteImageUrl == null ? getWxSessionImageUrl() : wxFavoriteImageUrl;
    }

    public ShareSDK setWxFavoriteImageUrl(String wxFavoriteImageUrl) {
        this.wxFavoriteImageUrl = wxFavoriteImageUrl;
        return this;
    }

    public String getWxFavoritePicPath() {
        return wxFavoritePicPath == null ? getWxSessionPicPath() : wxFavoritePicPath;
    }

    public ShareSDK setWxFavoritePicPath(String wxFavoritePicPath) {
        this.wxFavoritePicPath = wxFavoritePicPath;
        return this;
    }

    public String getWxFavoriteProductUrl() {
        return wxFavoriteProductUrl == null ? getWxSessionProductUrl() : wxFavoriteProductUrl;
    }

    public ShareSDK setWxFavoriteProductUrl(String wxFavoriteProductUrl) {
        this.wxFavoriteProductUrl = wxFavoriteProductUrl;
        return this;
    }

    public ShareCallBack getShareCallBack() {
        return shareCallBack;
    }

    public ShareSDK setShareCallBack(ShareCallBack shareCallBack) {
        this.shareCallBack = shareCallBack;
        return this;
    }

    public ShareCallBack getWxShareCallBack() {
        return wxShareCallBack;
    }

    public ShareSDK setWxShareCallBack(ShareCallBack wxShareCallBack) {
        this.wxShareCallBack = wxShareCallBack;
        return this;
    }

    public ShareCallBack getWeiboShareCallBack() {
        return weiboShareCallBack;
    }

    public ShareSDK setWeiboShareCallBack(ShareCallBack weiboShareCallBack) {
        this.weiboShareCallBack = weiboShareCallBack;
        return this;
    }

    public ShareCallBack getQqShareCallBack() {
        return qqShareCallBack;
    }

    public ShareSDK setQqShareCallBack(ShareCallBack qqShareCallBack) {
        this.qqShareCallBack = qqShareCallBack;
        return this;
    }

    public ShareCallBack getCopyShareCallBack() {
        return copyShareCallBack;
    }

    public ShareSDK setCopyShareCallBack(ShareCallBack copyShareCallBack) {
        this.copyShareCallBack = copyShareCallBack;
        return this;
    }

    public ShareCallBack getSmsShareCallBack() {
        return smsShareCallBack;
    }

    public ShareSDK setSmsShareCallBack(ShareCallBack smsShareCallBack) {
        this.smsShareCallBack = smsShareCallBack;
        return this;
    }

    public Set<Integer> getShareTos() {
        return shareTos;
    }

    public ShareSDK setShareTo(Integer shareTo) {
        if (shareTos.contains(shareTo)) {
            return this;
        }
        if (shareTo == ShareConstant.ALL) {
            this.shareTos.add(ShareConstant.WECHAT);
            this.shareTos.add(ShareConstant.WECHAT_TIMELINE);
            this.shareTos.add(ShareConstant.WECHAT_FAVOURITE);
            this.shareTos.add(ShareConstant.WEIBO);
            this.shareTos.add(ShareConstant.QQ);
            this.shareTos.add(ShareConstant.MESSAGE);
            this.shareTos.add(ShareConstant.LINK);
            return this;
        }
        this.shareTos.add(shareTo);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ShareSDK setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ShareSDK setContent(String content) {
        this.content = content;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ShareSDK setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getPicPath() {
        return picPath;
    }

    public ShareSDK setPicPath(String picPath) {
        this.picPath = picPath;
        return this;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public ShareSDK setProductUrl(String productUrl) {
        this.productUrl = productUrl;
        return this;
    }

    public String getWxSessionTitle() {
        return wxSessionTitle == null ? title : wxSessionTitle;
    }

    public ShareSDK setWxSessionTitle(String wxSessionTitle) {
        this.wxSessionTitle = wxSessionTitle;
        return this;
    }

    public String getWxSessionContent() {
        return wxSessionContent == null ? content : wxSessionContent;
    }

    public ShareSDK setWxSessionContent(String wxSessionContent) {
        this.wxSessionContent = wxSessionContent;
        return this;
    }

    public String getWxSessionImageUrl() {
        return wxSessionImageUrl == null ? imageUrl : wxSessionImageUrl;
    }

    public ShareSDK setWxSessionImageUrl(String wxSessionImageUrl) {
        this.wxSessionImageUrl = wxSessionImageUrl;
        return this;
    }

    public String getWxSessionPicPath() {
        return wxSessionPicPath == null ? picPath : wxSessionPicPath;
    }

    public ShareSDK setWxSessionPicPath(String wxSessionPicPath) {
        this.wxSessionPicPath = wxSessionPicPath;
        return this;
    }

    public String getWxSessionProductUrl() {
        return wxSessionProductUrl == null ? productUrl : wxSessionProductUrl;
    }

    public ShareSDK setWxSessionProductUrl(String wxSessionProductUrl) {
        this.wxSessionProductUrl = wxSessionProductUrl;
        return this;
    }

    public String getWeiboTitle() {
        return weiboTitle == null ? title : weiboTitle;
    }

    public ShareSDK setWeiboTitle(String weiboTitle) {
        this.weiboTitle = weiboTitle;
        return this;
    }

    public String getWeiboContent() {
        return weiboContent == null ? content : weiboContent;
    }

    public ShareSDK setWeiboContent(String weiboContent) {
        this.weiboContent = weiboContent;
        return this;
    }

    public String getWeiboImageUrl() {
        return weiboImageUrl == null ? imageUrl : weiboImageUrl;
    }

    public ShareSDK setWeiboImageUrl(String weiboImageUrl) {
        this.weiboImageUrl = weiboImageUrl;
        return this;
    }

    public String getWeiboPicPath() {
        return weiboPicPath == null ? picPath : weiboPicPath;
    }

    public ShareSDK setWeiboPicPath(String weiboPicPath) {
        this.weiboPicPath = weiboPicPath;
        return this;
    }

    public String getWeiboProductUrl() {
        return weiboProductUrl == null ? productUrl : weiboProductUrl;
    }

    public ShareSDK setWeiboProductUrl(String weiboProductUrl) {
        this.weiboProductUrl = weiboProductUrl;
        return this;
    }

    public String getQqTitle() {
        return qqTitle == null ? title : qqTitle;
    }

    public ShareSDK setQqTitle(String qqTitle) {
        this.qqTitle = qqTitle;
        return this;
    }

    public String getQqContent() {
        return qqContent == null ? content : qqContent;
    }

    public ShareSDK setQqContent(String qqContent) {
        this.qqContent = qqContent;
        return this;
    }

    public String getQqImageUrl() {
        return qqImageUrl == null ? imageUrl : qqImageUrl;
    }

    public ShareSDK setQqImageUrl(String qqImageUrl) {
        this.qqImageUrl = qqImageUrl;
        return this;
    }

    public String getQqPicPath() {
        return qqPicPath == null ? picPath : qqPicPath;
    }

    public ShareSDK setQqPicPath(String qqPicPath) {
        this.qqPicPath = qqPicPath;
        return this;
    }

    public String getQqProductUrl() {
        return qqProductUrl == null ? productUrl : qqProductUrl;
    }

    public ShareSDK setQqProductUrl(String qqProductUrl) {
        this.qqProductUrl = qqProductUrl;
        return this;
    }

    public String getCopyTitle() {
        return copyTitle == null ? title : copyTitle;
    }

    public ShareSDK setCopyTitle(String copyTitle) {
        this.copyTitle = copyTitle;
        return this;
    }

    public String getCopyContent() {
        return copyContent == null ? content : copyContent;
    }

    public ShareSDK setCopyContent(String copyContent) {
        this.copyContent = copyContent;
        return this;
    }

    public String getCopyImageUrl() {
        return copyImageUrl == null ? imageUrl : copyImageUrl;
    }

    public ShareSDK setCopyImageUrl(String copyImageUrl) {
        this.copyImageUrl = copyImageUrl;
        return this;
    }

    public String getCopyPicPath() {
        return copyPicPath == null ? picPath : copyPicPath;
    }

    public ShareSDK setCopyPicPath(String copyPicPath) {
        this.copyPicPath = copyPicPath;
        return this;
    }

    public String getCopyProductUrl() {
        return copyProductUrl == null ? productUrl : copyProductUrl;
    }

    public ShareSDK setCopyProductUrl(String copyProductUrl) {
        this.copyProductUrl = copyProductUrl;
        return this;
    }

    public String getSmsTitle() {
        return smsTitle == null ? title : smsTitle;
    }

    public ShareSDK setSmsTitle(String smsTitle) {
        this.smsTitle = smsTitle;
        return this;
    }

    public String getSmsContent() {
        return smsContent == null ? content : smsContent;
    }

    public ShareSDK setSmsContent(String smsContent) {
        this.smsContent = smsContent;
        return this;
    }

    public String getSmsImageUrl() {
        return smsImageUrl == null ? imageUrl : smsImageUrl;
    }

    public ShareSDK setSmsImageUrl(String smsImageUrl) {
        this.smsImageUrl = smsImageUrl;
        return this;
    }

    public String getSmsPicPath() {
        return smsPicPath == null ? picPath : smsPicPath;
    }

    public ShareSDK setSmsPicPath(String smsPicPath) {
        this.smsPicPath = smsPicPath;
        return this;
    }

    public String getSmsProductUrl() {
        return smsProductUrl == null ? productUrl : smsProductUrl;
    }

    public ShareSDK setSmsProductUrl(String smsProductUrl) {
        this.smsProductUrl = smsProductUrl;
        return this;
    }

    public void startShare() {
        new ShareUtil(activity).generate(this);
    }
}
