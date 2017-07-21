package cn.steve.webview.simple;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by yantinggeng on 2015/12/14.
 */
public class WebAppInterface {

    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast2(String toast) {
        Toast.makeText(mContext, "WebAppInterface" + toast, Toast.LENGTH_SHORT).show();
    }
}