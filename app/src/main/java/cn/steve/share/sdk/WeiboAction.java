package cn.steve.share.sdk;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by SteveYan on 2017/5/3.
 */

public class WeiboAction implements ShareAction {

    private Context context;

    public WeiboAction(Context context) {
        this.context = context;
    }

    @Override
    public void share(ShareData shareData, ShareCallBack shareCallBack) {
        Toast.makeText(context, shareData.getTitle(), Toast.LENGTH_SHORT).show();

    }
}
