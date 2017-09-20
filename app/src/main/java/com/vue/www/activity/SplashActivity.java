package com.vue.www.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gyf.barlibrary.ImmersionBar;
import com.vue.www.R;

public class SplashActivity extends BaseActivity {
    private Splashhandler splashhandler;
    private Handler x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSplash();
        ImmersionBar.with(this).transparentNavigationBar().init();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_splash;
    }

    class Splashhandler implements Runnable{
        public void run() {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }
    public void showSplash() {
        x = new Handler();
        splashhandler = new Splashhandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(x!=null&&splashhandler!=null){
            x.removeCallbacks(splashhandler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(x!=null&&splashhandler!=null){
            x.postDelayed(splashhandler, 2000);
        }
    }
}


