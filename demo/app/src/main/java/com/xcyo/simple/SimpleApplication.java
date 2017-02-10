package com.xcyo.simple;

import android.app.Application;

import com.xcyo.sdk.api.YoyoApi;
import com.xutils.x;

/**
 * Created by caixiaoxiao on 9/10/16.
 */
public class SimpleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);

        YoyoApi.setTestEnv(true);
        YoyoApi.setDebugMode(true);
        YoyoApi.init(this, "10001", "12345", null);
    }
}
