package cn.steve.webview.simple;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by SteveYan on 2017/7/12.
 */

public class H5Plugin {

    Context mContext;

    public H5Plugin(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
