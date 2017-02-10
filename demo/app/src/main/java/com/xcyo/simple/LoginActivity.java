package com.xcyo.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoServerInterface;

/**
 * Created by bear on 2017/1/13.
 */

public class LoginActivity extends FragmentActivity {
    String openId = "maxiaotao111";
    String token = "aelOxc-_YIkWUW1dLVEIKF467DWKDeYJzibRHp11v7vCo4o8Q3hKrZug2VQF-" +
            "doGH1MmS2OS5EY5%0D%0AFPVpTK2EI0vySnngwpBEzKHi-" +
            "j2_eFuL6uDN3sByN89eGolWP9dv1sLCyGA2lWpjLh4K3gDFmpGo%0D%0AgPC3-SPRVcnZs933698%0D%0A";
    private Button btn_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((TextView)findViewById(R.id.login_openid)).setText("openId : " + openId);
        ((TextView)findViewById(R.id.login_token)).setText("token : " + token);
        btn_login = (Button) findViewById(R.id.demo_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
                btn_login.setClickable(false);
                btn_login.setText("正在登录中");
            }
        });
    }

    /***
     * sdk登录
     */
    private void goLogin(){
        YoyoApi.loginYoyo(openId, token, new YoyoServerInterface<Boolean>() {
            @Override
            public boolean onOk(Boolean response) {
                Log.e("YoyoSDK","登录成功");
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                Log.e("YoyoSDK", "登录失败");
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                btn_login.setClickable(true);
                btn_login.setText("登录");
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
