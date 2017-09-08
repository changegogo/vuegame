package com.vue.www.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
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
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vue.www.R;
import com.vue.www.receiver.NetWorkStateReceiver;
import com.vue.www.utils.NetWorkCheck;
import com.vue.www.view.CustomDialog;
import com.vue.www.view.ToastSelf;

import static com.vue.www.utils.NetWorkCheck.check;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mRLayout;
    private WebView mWebView;
    private boolean mIsError;
    private LinearLayout mReturn;
    private TextView mTitle;
    private TextView mClose;
    private NetWorkStateReceiver netWorkStateReceiver;
    private ToastSelf mToastSelf;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
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

        mWebView.addJavascriptInterface(new AndroidtoJs(), "myObj");

        mWebView.loadUrl("file:///android_asset/index.html");
    }

    public class AndroidtoJs extends Object{
        @JavascriptInterface
        public boolean checknet(String param){
            Log.i("AndroidtoJs", param);
            return NetWorkCheck.check(MainActivity.this);
        }
    }

    private void initView() {
        mRLayout = (RelativeLayout) findViewById(R.id.load_error_layout);
        mWebView = (WebView) findViewById(R.id.web_view);
        mReturn = (LinearLayout) findViewById(R.id.return_layout);
        mTitle = (TextView) findViewById(R.id.title_content);
        mClose = (TextView) findViewById(R.id.close_main);
        mToastSelf = new ToastSelf(this);
    }

    private void initEvent(){
        mRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView!=null && mWebView.getVisibility()==View.INVISIBLE){
                    mRLayout.setClickable(false);
                    mIsError = false;
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    }
                   // mWebView.reload();
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
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
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
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if ( uri.getScheme().equals("js")) {
                if (uri.getAuthority().equals("webview")) {
                    check(MainActivity.this, mToastSelf);
                }
                return true;
            }
            if(!TextUtils.isEmpty(url) && (url.endsWith("apk") || url.contains("download"))){
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
            // 设置标题
            if(url.contains("game")){
                MainActivity.this.setTitle("游戏");
            }else if(url.contains("openarea")){
                MainActivity.this.setTitle("开服");
            }else if(url.contains("search")){
                MainActivity.this.setTitle("搜索");
            }else if(url.contains("rank")){
                MainActivity.this.setTitle("排行");
            }

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
            Log.i("load", "加载错误->failingUrl:"+failingUrl);
            mRLayout.setClickable(true);
            mIsError = true;
            showErrorPage();
        }
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
                MainActivity.this.setTitle("请检查网络");
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
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);

       /* if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*/
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private CustomDialog mDialog;
    private void showProgressDialog(){
        if(mDialog == null){
            mDialog = new CustomDialog(this, R.style.CustomDialog);
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
        unregisterReceiver(netWorkStateReceiver);
        mToastSelf.cancelToast();
        if (mWebView != null){
            mWebView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册netWorkStateReceiver");
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
