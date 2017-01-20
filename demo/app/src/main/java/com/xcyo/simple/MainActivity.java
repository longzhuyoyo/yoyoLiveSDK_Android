package com.xcyo.simple;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoEvent;
import com.xcyo.sdk.api.request.YoyoEventInterface;
import com.xcyo.sdk.api.request.YoyoServerInterface;
import com.xcyo.simple.simadapter.SimpleAdapter;
import com.xcyo.yoyo.record.server.YoyoSharedRecord;
/**
 * Created by caixiaoxiao on 26/9/16.
 */
public class MainActivity extends FragmentActivity {
    public YoyoSharedRecord yoyoSharedRecord;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private Button mLoginBtn,mRoomListBtn,mEnterRoomBtn,mShareBtn,mUserInfoBtn,mSettingBtn;
    private EditText mCustomRoomIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginBtn = (Button) findViewById(R.id.main_login);
        mRoomListBtn = (Button) findViewById(R.id.main_room_list);
        mEnterRoomBtn = (Button) findViewById(R.id.main_enter_room1);
        mShareBtn = (Button) findViewById(R.id.main_share_data);
        mUserInfoBtn = (Button) findViewById(R.id.main_user_info);
        mSettingBtn = (Button) findViewById(R.id.main_setting);
        mCustomRoomIdText = (EditText) findViewById(R.id.main_room_id);
        mCustomRoomIdText.setText("1012039955");
        setOnClick();

        setAction();

        //设置视频播放器
        YoyoApi.setMediaPlayerClazz(KSYVideoView.class);
        //设置事件回调
        YoyoApi.setEventCallBack(new YoyoEventInterface() {
            @Override
            public void onEvent(YoyoEvent event, FragmentActivity frag) {
                switch (event) {
                    case YoyoEventLogin:
                        showLoginDialog(frag);
                        break;
                    case YoyoEventExchange:
                        goExchange();
                        break;
                    case YoyoEventShare:
                        showShareInfo(frag);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLogin = !TextUtils.isEmpty(AppHelper.user.openId);
        mLoginBtn.setVisibility(isLogin ? View.GONE : View.VISIBLE);
        mUserInfoBtn.setVisibility(!isLogin ? View.GONE : View.VISIBLE);
    }

    private void setOnClick(){
        //登录
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog(MainActivity.this);
            }
        });

        //个人信息
        mUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                startActivity(intent);
            }
        });

        //获取房间列表
        mRoomListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SingerListActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //获得分享数据
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoyoApi.getSharedData(new YoyoServerInterface<YoyoSharedRecord>() {
                    @Override
                    public boolean onOk(YoyoSharedRecord response) {
                        if (response != null) {
                            yoyoSharedRecord = response;
                        }
                        return true;
                    }

                    @Override
                    public boolean onError(YoyoErrorCode err, String msg) {
                        Toast.makeText(MainActivity.this, "share ->" + msg, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });

        //进入房间
        mEnterRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = mCustomRoomIdText.getText().toString();
                YoyoApi.enterRoom(roomId);
            }
        });

        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showLoginDialog(FragmentActivity fragmentActivity) {
        Intent intent = new Intent(fragmentActivity,LoginActivity.class);
        fragmentActivity.startActivity(intent);
    }

    public void showShareInfo(final FragmentActivity frag){
        YoyoApi.getSharedData(new YoyoServerInterface<YoyoSharedRecord>() {
            @Override
            public boolean onOk(YoyoSharedRecord response) {
                if (response != null) {
                    yoyoSharedRecord = response;
                    showShareData(frag);
                }
                return true;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                return true;
            }
        });
    }

    public void showShareData(FragmentActivity frag) {
        AlertDialog alertDialog = new AlertDialog.Builder(frag).setTitle("分享信息")
                .setMessage("宣传图：" + yoyoSharedRecord.getImgUrl() + "\n\n链接：" + yoyoSharedRecord.getUrl()
                        + "\n\n主播昵称：" + yoyoSharedRecord.getAlias()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    private void goExchange(){
        Intent mIntent = new Intent(this, ExchangeActivity.class);
        startActivity(mIntent);
    }

    void showUser(){
        mUserInfoBtn.setVisibility(View.VISIBLE);
        mLoginBtn.setVisibility(View.GONE);
    }

    private void setAction(){
        AppHelper.setOk(new AppHelper.Ok() {
            @Override
            public void isOk(int tag) {
                if (tag == 1) {
                    showUser();
                }
            }
        });
    }
}
