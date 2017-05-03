package cn.steve.share.sdk;

import android.content.Context;
import android.widget.Toast;

/**
 * 微信分享的详细功能实现
 *
 * Created by SteveYan on 2017/5/3.
 */

public class WXAction implements ShareAction {

    private Context context;

    public WXAction(Context context) {
        this.context = context;
    }

    @Override
    public void share(ShareData shareData, ShareCallBack shareCallBack) {
        Toast.makeText(context, shareData.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
