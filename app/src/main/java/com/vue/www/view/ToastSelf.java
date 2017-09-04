package com.vue.www.view;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Author: dlw on 2017/9/4 14:57
 * Email: dailongshao@126.com
 */

public class ToastSelf {
    private static Toast mToast;
    private static TextView mTextView;
    private boolean isShowToast;

    public  ToastSelf(Context context){
        mToast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
    }

    public void showToast(String str) {
        mToast.setText(str);
        mToast.show();
        isShowToast = true;
    }

    public void cancelToast(){
        if(isShowToast == true){
            mToast.cancel();
        }
    }

}
