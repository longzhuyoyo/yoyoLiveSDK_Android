package com.xcyo.simple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xcyo.sdk.api.YoyoApi;

/**
 * Created by caixiaoxiao on 17/11/16.
 */
public class NetworkChangedReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activieNetInfo = connectivityManager.getActiveNetworkInfo();
//        boolean isWifi = false;
        if (activieNetInfo != null){
            if (activieNetInfo.isAvailable() && activieNetInfo.isConnected()){
                if (activieNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    //wifi环境下一直都可以播放视频
                    YoyoApi.setVideoCanPlay(true);
                }else {
                    //非wifi情况下根据需求来选择可不可以播放视频
                    YoyoApi.setVideoCanPlay(false);
                }
            }
        }
    }
}
