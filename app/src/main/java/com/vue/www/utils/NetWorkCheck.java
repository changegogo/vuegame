package com.vue.www.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.vue.www.view.ToastSelf;

/**
 * Author: dlw on 2017/9/4 14:27
 * Email: dailongshao@126.com
 */

public class NetWorkCheck {
    public static void check(Context context, ToastSelf toast){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(!wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()){
                if(toast != null){
                    toast.showToast("网络未连接!");
                }
            }
            //API大于23时使用下面的方式进行网络监听
        }else {
            //System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            if(networks.length <= 0){
                if(toast != null){
                    toast.showToast("网络未连接!");
                }
            }
        }
    }

    public static boolean check(Context context){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(!wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()){
                return false;
            }else{
                return true;
            }
        }else {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connMgr.getAllNetworks();
            if(networks.length <= 0){
                return false;
            }else{
                return true;
            }
        }
    }
}
