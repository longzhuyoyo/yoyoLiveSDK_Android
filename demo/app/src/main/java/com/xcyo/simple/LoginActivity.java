package com.xcyo.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoServerInterface;

/**
 * Created by bear on 2017/1/13.
 */

public class LoginActivity extends FragmentActivity {
    private EditText editUser;
    private EditText editPw;
    private Button btn_login;

    private AppHelper.Ok ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editUser = (EditText)findViewById(R.id.login_user);
        editPw = (EditText)findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.demo_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUser.getText().toString();
                String password = editPw.getText().toString();
                AppHelper.doLogin(username, password);
                btn_login.setClickable(false);
                btn_login.setText("正在登录中");
            }
        });
        AppHelper.setOk(ok = new AppHelper.Ok() {
            @Override
            public void isOk(int tag) {
                if (tag == 1) {
                    //sdk登录
                    goLogin();
                } else if (tag == 4) {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    btn_login.setClickable(true);
                    btn_login.setText("登录");
                }
            }
        });
    }

    /***
     * sdk登录
     */
    private void goLogin(){
        YoyoApi.loginYoyo(AppHelper.user.openId, AppHelper.user.token, new YoyoServerInterface<Boolean>() {
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
        AppHelper.removeOK(ok);
    }
}
