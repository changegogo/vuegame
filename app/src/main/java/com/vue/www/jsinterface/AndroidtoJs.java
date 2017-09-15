package com.vue.www.jsinterface;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.vue.www.activity.PhotoBrowserActivity;
import com.vue.www.utils.NetWorkCheck;

import static android.util.Log.i;

/**
 * Author: dlw on 2017/9/15 14:20
 * Email: dailongshao@126.com
 */

public class AndroidtoJs {
    private Context context;
    public AndroidtoJs(Context context){
        this.context = context;
    }
    /*
    *  js调用，检测网络返回boolean
    *  true 有网络
    *  false 无网络
    * */
    @JavascriptInterface
    public boolean checknet(String param){
        i("AndroidtoJs", param);
        return NetWorkCheck.check(context);
    }
    /*
    *  打开photoview
    *  查看大图
    * */
    @android.webkit.JavascriptInterface
    public void openImage(String index, String imgs) {
        Intent intent = new Intent();
        intent.putExtra("imageUrls", imgs);
        intent.putExtra("curImageIndex", index);
        intent.setClass(context, PhotoBrowserActivity.class);
        context.startActivity(intent);
    }
}
