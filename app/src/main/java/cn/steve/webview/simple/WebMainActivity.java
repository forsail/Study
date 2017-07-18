package cn.steve.webview.simple;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import cn.steve.camera.ImageChoosePlugin;
import cn.steve.study.R;

/**
 * Created by SteveYan on 2017/6/5.
 */

public class WebMainActivity extends Activity {

    private ImageChoosePlugin imageChoosePlugin = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView mWebView = (WebView) findViewById(R.id.webView1);
        String url = "file:///android_asset/inputFileTest.html";
        mWebView.loadUrl(url);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new MainWebChromeClient());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageChoosePlugin != null) {
            imageChoosePlugin.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class MainWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg, null);
        }

        @SuppressWarnings("unused")
        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg, null);
        }

        //Android 5.0+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooserImpl(null, filePathCallback);
            return true;
        }

        private void openFileChooserImpl(ValueCallback<Uri> uploadMessage, ValueCallback<Uri[]> filePathCallbackArray) {
            if (imageChoosePlugin == null) {
                imageChoosePlugin = new ImageChoosePlugin(WebMainActivity.this);
            }
            imageChoosePlugin.setValueCallback(uploadMessage, filePathCallbackArray);
            imageChoosePlugin.showMyDialog();
        }

    }

}
