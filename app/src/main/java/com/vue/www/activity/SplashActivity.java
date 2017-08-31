package com.vue.www.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vue.www.R;

public class SplashActivity extends AppCompatActivity {
    private Splashhandler splashhandler;
    private Handler x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showSplash();
    }

    class Splashhandler implements Runnable{
        public void run() {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
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
            x.postDelayed(splashhandler, 3000);
        }
    }
}


