package cn.steve.share.sdk;

/**
 * 分享的数据
 *
 * Created by SteveYan on 2017/4/28.
 */

public class ShareData {

    private String title;
    private String content;
    private String imageUrl;
    private String picPath;
    private String productUrl;

    private ShareData(ShareDataBuilder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.imageUrl = builder.imageUrl;
        this.picPath = builder.picPath;
        this.productUrl = builder.productUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public static class ShareDataBuilder {

        private String title;
        private String content;
        private String imageUrl;
        private String picPath;
        private String productUrl;

        public ShareDataBuilder() {
        }

        public ShareDataBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ShareDataBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public ShareDataBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ShareDataBuilder setPicPath(String picPath) {
            this.picPath = picPath;
            return this;
        }

        public ShareDataBuilder setProductUrl(String productUrl) {
            this.productUrl = productUrl;
            return this;
        }

        public ShareData build() {
            return new ShareData(this);
        }
    }
}
