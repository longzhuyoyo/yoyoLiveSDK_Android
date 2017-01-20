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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        TextView userInfoContentText = (TextView) findViewById(R.id.user_info_content);
        AppHelper.User user = AppHelper.user;
        String content = "\n"+
                "openId : " + user.openId + "\n"+
                "name : " + user.name + "\n" +
                "token : " + user.token + "\n"+
                "amount : " + user.amount;

        userInfoContentText.setText(content);

        //兑换悠币
        findViewById(R.id.user_info_exchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AppHelper.user.openId)) {
                    Toast.makeText(UserInfoActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                goExchange();
            }
        });


        //退出登录
        findViewById(R.id.user_info_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.doLogout();
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

    private void goExchange(){
        Intent mIntent = new Intent(this, ExchangeActivity.class);
        startActivity(mIntent);
    }

    public ArrayMap<String,String> getMap(String alias,String avatar){
        ArrayMap<String,String> map = new ArrayMap<>();
        if(!TextUtils.isEmpty(alias)){
            map.put("alias",alias);
        }
        if(!TextUtils.isEmpty(avatar)){
            map.put("avatar",avatar);
        }
        if(!map.isEmpty()){
            return map;
        }
        return null;
    }
}
