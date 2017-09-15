package com.vue.www.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vue.www.R;
import com.vue.www.jsinterface.AndroidtoJs;
import com.vue.www.view.CustomDialog;
import com.vue.www.view.ToastSelf;
import com.vue.www.webviewtool.MyDownloadListener;
import com.vue.www.webviewtool.MyWebChromeClient;
import com.vue.www.webviewtool.MyWebviewClient;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mRLayout;
    private WebView mWebView;
    private LinearLayout mReturn;
    private TextView mTitle;
    private TextView mClose;
    private ToastSelf mToastSelf;
    private CustomDialog mDialog;
    private MyWebviewClient mClient;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);
        //调用JS方法
        mWebSettings.setJavaScriptEnabled(true);
        saveData(mWebSettings);
        mClient = new MyWebviewClient(this, mDialog, mToastSelf, mRLayout);
        mWebView.setWebViewClient(mClient);
        mWebView.setWebChromeClient(new MyWebChromeClient(mDialog));
        mWebView.setDownloadListener(new MyDownloadListener(this));
        mWebView.addJavascriptInterface(new AndroidtoJs(this), "myObj");
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    protected void initView() {
        mRLayout = (RelativeLayout) findViewById(R.id.load_error_layout);
        mWebView = (WebView) findViewById(R.id.web_view);
        mReturn = (LinearLayout) findViewById(R.id.return_layout);
        mTitle = (TextView) findViewById(R.id.title_content);
        mClose = (TextView) findViewById(R.id.close_main);
        mToastSelf = new ToastSelf(this);
        mDialog = new CustomDialog(this, R.style.CustomDialog);
    }

    protected void setListener(){
        mRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView!=null && mWebView.getVisibility()==View.INVISIBLE) {
                    mRLayout.setClickable(false);
                    // 设置页面未加载出错
                    mClient.setIsError();
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    }
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

    public void setTitle(String title){
        if(mTitle!=null){
            mTitle.setText(title);
        }
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

        /*if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*/
    }
    private long mkeyTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
            if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                mkeyTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showProgressDialog(){
        if(mDialog == null){
            mDialog = new CustomDialog(this, R.style.CustomDialog);
            mDialog.show();
        }
    }
    private void closeProgressDialog(){
        if(mDialog!=null){
            mDialog.dismiss();
            mDialog=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToastSelf.cancelToast();
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
        if (mWebView != null) {
            Log.e("load", "webview destroy");
            mWebView.destroy();
        }
    }


}
