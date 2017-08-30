package com.vue.www;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private RelativeLayout mRLayout;
    private WebView mWebView;
    // http://app.xiaomi.com/home
    // http://xin.feicuiedu.com:3000/#/recommend
    private String[] mBaseUrl = {
            "http://app.xiaomi.com/home",
            "http://xin.feicuiedu.com:3000/#/recommend",
            "http://xin.feicuiedu.com:8088/feicuiwb/wap"
    };
    private boolean mIsError;
    private LinearLayout mReturn;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        mRLayout = (RelativeLayout) findViewById(R.id.load_error_layout);
        mWebView = (WebView) findViewById(R.id.web_view);
        mReturn = (LinearLayout) findViewById(R.id.return_layout);
        mTitle = (TextView) findViewById(R.id.title_content);
        //showProgressDialog();
        initEvent();

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);
        //调用JS方法
        mWebSettings.setJavaScriptEnabled(true);

        saveData(mWebSettings);
        newWin(mWebSettings);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setDownloadListener(downloadListener);
        //mBaseUrl[rand0_2()]
        mWebView.loadUrl(mBaseUrl[rand0_2()]);
    }

    private int rand0_2(){
        return (int)(Math.random()*3);
    }

    private void initEvent(){
        mRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView!=null && mWebView.getVisibility()==View.INVISIBLE){
                    mRLayout.setClickable(false);
                    mIsError = false;
                    mWebView.reload();
                }
            }
        });
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }else{
                    MainActivity.this.finish();
                }

            }
        });
    }

    DownloadListener downloadListener =  new DownloadListener(){

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Log.i("load", "url="+url);
            Log.i("load", "userAgent="+userAgent);
            Log.i("load", "contentDisposition="+contentDisposition);
            Log.i("load", "mimetype="+mimetype);
            Log.i("load", "contentLength="+contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    WebViewClient webViewClient = new WebViewClient(){
        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("load", "加载了");
            if(!TextUtils.isEmpty(url) && url.endsWith("apk")){
                //Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //startActivity(viewIntent);
            }else{
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showProgressDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.i("load", "加载完成:"+url);
            closeProgressDialog();
            mRLayout.setClickable(true);
            if(mIsError){
                mRLayout.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.INVISIBLE);
            }else{
                mRLayout.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.i("load", "加载错误");
            mRLayout.setClickable(true);
            mIsError = true;
            showErrorPage();
        }

        /*@Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }*/
    };

    protected void showErrorPage() {
        if(mIsError && mRLayout != null && mRLayout.getVisibility()==View.INVISIBLE ){
            mRLayout.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
        }
    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress>=100){
                Log.i("load", "关闭newProgress:"+newProgress);
                closeProgressDialog();
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.i("load", mIsError+"<-title->"+title);
            if(!mIsError && !title.contains(".")){
                MainActivity.this.setTitle(title);
            }else{
                MainActivity.this.setTitle("");
            }
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebView);
            resultMsg.sendToTarget();
            return true;
        }
    };

    private void setTitle(String title){
        if(mTitle!=null){
            mTitle.setText(title);
        }
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ProgressDialog mDialog;
    private void showProgressDialog(){
        if(mDialog==null){
            mDialog = new ProgressDialog(mContext);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mDialog.setMessage("正在加载 ，请等待...");
            mDialog.setIndeterminate(false);//设置进度条是否为不明确
            mDialog.setCancelable(true);//设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog=null;
                }
            });
            mDialog.show();
            Log.i("load", "mDialog show");
        }
    }
    private void closeProgressDialog(){
        if(mDialog!=null){
            mDialog.dismiss();
            mDialog=null;
            Log.i("load", "mDialog hide");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null){
            mWebView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null){
            mWebView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((RelativeLayout) mWebView.getParent()).removeView(mWebView);
        if (mWebView != null) {
            Log.e("load", "webview destroy");
            mWebView.destroy();
        }
    }
}
