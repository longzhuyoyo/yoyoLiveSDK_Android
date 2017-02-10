package com.xcyo.simple;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoServerInterface;

/**
 * Created by caixiaoxiao on 6/1/17.
 */
public class UserInfoActivity extends FragmentActivity{
    String openId = "maxiaotao111";
    String token = "aelOxc-_YIkWUW1dLVEIKF467DWKDeYJzibRHp11v7vCo4o8Q3hKrZug2VQF-" +
            "doGH1MmS2OS5EY5%0D%0AFPVpTK2EI0vySnngwpBEzKHi-" +
            "j2_eFuL6uDN3sByN89eGolWP9dv1sLCyGA2lWpjLh4K3gDFmpGo%0D%0AgPC3-SPRVcnZs933698%0D%0A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        TextView userInfoContentText = (TextView) findViewById(R.id.user_info_content);
        String content = "\n"+
                "openId : " + openId + "\n"+
                "token : " + token + "\n";

        userInfoContentText.setText(content);

        //退出登录
        findViewById(R.id.user_info_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出sdk
                YoyoApi.logoutYoyo(new YoyoServerInterface<Boolean>() {
                    @Override
                    public boolean onOk(Boolean response) {
                        if (response) {
                            Toast.makeText(UserInfoActivity.this, "logout 蓝瘦 香菇", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }

                    @Override
                    public boolean onError(YoyoErrorCode err, String msg) {
                        return true;
                    }
                });
                finish();
            }
        });
    }
}
