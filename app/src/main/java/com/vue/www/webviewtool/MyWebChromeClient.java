package com.vue.www.webviewtool;

import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.vue.www.view.CustomDialog;


public class MyWebChromeClient extends WebChromeClient {
    private CustomDialog mDialog;
    public MyWebChromeClient(CustomDialog dialog) {
        this.mDialog = dialog;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        Log.i("title", title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if(newProgress>=100){
           if(mDialog!=null){
               mDialog.dismiss();
           }
        }
    }

    // For Android > 5.0
    public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
        //openFileChooserImplForAndroid5(uploadMsg);
        return true;
    }
}
