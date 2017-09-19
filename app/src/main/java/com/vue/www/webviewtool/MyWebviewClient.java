package com.vue.www.webviewtool;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.vue.www.activity.MainActivity;
import com.vue.www.view.CustomDialog;
import com.vue.www.view.ToastSelf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.i;
import static com.vue.www.utils.NetWorkCheck.check;


public class MyWebviewClient extends WebViewClient {
    private Context context;
    private ToastSelf mToastSelf;
    private CustomDialog mDialog;
    private boolean mIsError;
    private RelativeLayout mRLayout;
    public MyWebviewClient(Context context, CustomDialog dialog, ToastSelf toast,RelativeLayout layout) {
        this.context = context;
        this.mToastSelf = toast;
        this.mRLayout = layout;
        this.mDialog = dialog;
    }

    public void setIsError(){
        this.mIsError = false;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if ( uri.getScheme().equals("js")) {
            if (uri.getAuthority().equals("webview")) {
                check(context, mToastSelf);
            }
            return true;
        }
        if(!TextUtils.isEmpty(url) && (url.endsWith("apk") || url.contains("download"))){

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
        if(isNumberEnd(url)){
            Log.i("load", "以数字结尾");
        }else{
            // 设置标题
            if(url.contains("game")){
                ((MainActivity)context).setTitle("游戏");
            }else if(url.contains("openarea")){
                ((MainActivity)context).setTitle("开服");
            }else if(url.contains("search")){
                ((MainActivity)context).setTitle("搜索");
            }else if(url.contains("rank")){
                ((MainActivity)context).setTitle("排行");
            }
        }
        closeProgressDialog();
        mRLayout.setClickable(true);
        if(mIsError){
            mRLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        }else{
            mRLayout.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        i("load", "加载错误->failingUrl:"+failingUrl);
        mRLayout.setClickable(true);
        mIsError = true;
        showErrorPage(view);
    }

    private void showProgressDialog(){
        if(mDialog != null){
            mDialog.show();
        }
    }
    private void closeProgressDialog(){
        if(mDialog!=null){
            mDialog.dismiss();
        }
    }

    protected void showErrorPage(WebView view) {
        if(mIsError && mRLayout != null && mRLayout.getVisibility()==View.INVISIBLE ){
            mRLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        }
    }
    // 判断字符串是否以数字结尾
    private boolean isNumberEnd(String str){
        Pattern pattern = Pattern.compile("\\d+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
