package com.vue.www.webviewtool;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.vue.www.view.CustomDialog;


public class MyWebChromeClient extends WebChromeClient {
    private CustomDialog mDialog;
    public MyWebChromeClient(CustomDialog dialog) {
        this.mDialog = dialog;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if(newProgress>=100){
           if(mDialog!=null){
               mDialog.dismiss();
           }
        }
    }
}
