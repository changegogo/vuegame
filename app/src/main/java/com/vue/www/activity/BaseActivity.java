package com.vue.www.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

/**
 * Author: dlw on 2017/9/12 11:11
 * Email: dailongshao@126.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        //初始化沉浸式
        if (isImmersionBarEnabled())
            initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
        //设置监听
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //在BaseActivity里销毁
    }

    protected abstract int setLayoutId();

    protected void initImmersionBar() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void setListener() {
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }
}
