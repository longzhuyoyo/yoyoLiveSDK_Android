package com.xcyo.simple;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import com.xcyo.yoyo.utils.*;

/**
 * Created by caixiaoxiao on 26/9/16.
 */
public class MainActivity extends FragmentActivity {
    public YoyoSharedRecord yoyoSharedRecord;
    private Button mLoginBtn,mRoomListBtn,mEnterHotRoomBtn,mEnterRoomBtn,mShareBtn,mUserInfoBtn,mSettingBtn,mClearBtn;
    private EditText mCustomRoomIdText;


    private NetworkChangedReceiver mNetworkReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginBtn = (Button) findViewById(R.id.main_login);
        mRoomListBtn = (Button) findViewById(R.id.main_room_list);
        mEnterHotRoomBtn = (Button) findViewById(R.id.main_hot_room);
        mEnterRoomBtn = (Button) findViewById(R.id.main_enter_room1);
        mShareBtn = (Button) findViewById(R.id.main_share_data);
        mUserInfoBtn = (Button) findViewById(R.id.main_user_info);
        mSettingBtn = (Button) findViewById(R.id.main_setting);
        mCustomRoomIdText = (EditText) findViewById(R.id.main_room_id);
        mClearBtn = (Button) findViewById(R.id.main_clear);
        mCustomRoomIdText.setText("1012039955");
        setOnClick();

        setCacheText();
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
                    case YoyoEventShare:
                        showShareInfo(frag);
                        break;
                    default:
                        break;
                }
            }
        });

        registerNetworkReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLogin = YoyoApi.isLogin();
        mLoginBtn.setVisibility(isLogin ? View.GONE : View.VISIBLE);
        mUserInfoBtn.setVisibility(!isLogin ? View.GONE : View.VISIBLE);
        setCacheText();
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
                Intent intent = new Intent(MainActivity.this, SingerListActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //进入热门主播房间
        mEnterHotRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoyoApi.enterRandomHotRoom(null);
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
                final String roomId = mCustomRoomIdText.getText().toString();
                YoyoApi.enterRoom(roomId);
            }
        });

        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        //清理缓存
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoyoApi.clearCache();
                setCacheText();
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

    /**
     * 显示缓存大小
     */
    private void setCacheText(){
        long num = YoyoApi.getCacheSize();
        mClearBtn.setText("清理缓存: " + num);
    }

    @Override
    protected void onDestroy() {
        unRegisterNetworkReceiver();
        super.onDestroy();
    }

    /***
     * 监听网络变化
     */
    private void registerNetworkReceiver(){
        mNetworkReceiver = new NetworkChangedReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    /***
     * 注销网络变化监听
     */
    private void unRegisterNetworkReceiver(){
        unregisterReceiver(mNetworkReceiver);
    }
}
