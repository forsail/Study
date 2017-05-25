package cn.steve.share;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * 新浪微博授权处理
 * Created by SteveYan on 2017/5/25.
 */

public class WBAuthProcessor {
    private Activity activity;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    public WBAuthProcessor(Activity activity) {
        this.activity = activity;
        mSsoHandler = new SsoHandler(activity);
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            /* 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
            if (token.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(activity, token);
                Toast.makeText(activity, "授权成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void cancel() {
            Toast.makeText(activity, "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(activity, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
