package com.xcyo.simple;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoEvent;
import com.xcyo.sdk.api.request.YoyoEventInterface;
import com.xcyo.sdk.api.request.YoyoServerInterface;
import com.xcyo.simple.simadapter.SimpleAdapter;
import com.xcyo.yoyo.chat.ChatMessageParseHandler;
import com.xcyo.yoyo.record.server.YoyoHomeIndexRecord;
import com.xcyo.yoyo.record.server.YoyoIndexRecord;
import com.xcyo.yoyo.record.server.YoyoSharedRecord;
import com.xcyo.yoyo.ui.activity.room.MediaRoomActivity;
import com.xcyo.yoyo.video.YoyoSystemVideoView;

import java.util.ArrayList;

/**
 * Created by caixiaoxiao on 26/9/16.
 */
public class MainActivity extends FragmentActivity {
    public YoyoSharedRecord yoyoSharedRecord;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ArrayMap<String, String> arrayMap;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                showDialogList();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAction();

        //登录
        findViewById(R.id.main_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        //退出登录
        findViewById(R.id.main_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoyoApi.logoutYoyo(new YoyoServerInterface<Boolean>() {
                    @Override
                    public boolean onOk(Boolean response) {
                        if (response) {
                            Toast.makeText(MainActivity.this, "logout 蓝瘦 香菇", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }

                    @Override
                    public boolean onError(YoyoErrorCode err, String msg) {
                        return true;
                    }
                });
            }
        });
        //设置播放器
        RadioGroup videoRG = (RadioGroup) findViewById(R.id.main_video_rg);
        videoRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.main_video_view) {
                    YoyoApi.setMediaPlayerClazz(YoyoSystemVideoView.class);
                } else if (checkedId == R.id.main_ijkvideo_view) {
                    YoyoApi.setMediaPlayerClazz(YoyoIjkVideoView.class);
                } else if (checkedId == R.id.main_ksyvideo_view) {
                    YoyoApi.setMediaPlayerClazz(KSYVideoView.class);
                }
            }
        });

        //修改用户信息
        findViewById(R.id.main_update_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });

        //获取房间列表
        findViewById(R.id.main_room_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoyoApi.getHostList(new YoyoServerInterface<YoyoHomeIndexRecord>() {
                    @Override
                    public boolean onOk(YoyoHomeIndexRecord response) {
                        if (response != null) {
                            simpleAdapter = new SimpleAdapter(MainActivity.this, new ArrayList<YoyoIndexRecord>());
                            simpleAdapter.setList(response.getList());
                            handler.sendEmptyMessage(1);
                        }
                        return true;
                    }

                    @Override
                    public boolean onError(YoyoErrorCode err, String msg) {
                        Toast.makeText(MainActivity.this, "list ->" + msg, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });


        //打开分享功能 默认关闭
        YoyoApi.setShareAble(true);
        //获得分享数据
        findViewById(R.id.main_share_data).setOnClickListener(new View.OnClickListener() {
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

        //兑换悠币
        findViewById(R.id.main_exchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(AppHelper.user.openId)){
                    Toast.makeText(MainActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                    return ;
                }
                goExchange();
            }
        });



        //进入房间
        final EditText roomIdText = (EditText) findViewById(R.id.main_room_id);
        roomIdText.setText("1012039955");
        findViewById(R.id.main_enter_room1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = roomIdText.getText().toString();
                YoyoApi.enterRoom(roomId);
            }
        });

        findViewById(R.id.main_enter_room2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = roomIdText.getText().toString();
                if (TextUtils.isEmpty(roomId)) {
                    Toast.makeText(MainActivity.this, "房间号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(YoyoApi.mContext, MediaRoomActivity.class);
                intent.putExtra("uid", roomId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                YoyoApi.mContext.startActivity(intent);
            }
        });

        //设置事件回调
        YoyoApi.setEventCallBack(new YoyoEventInterface() {
            @Override
            public void onEvent(YoyoEvent event , FragmentActivity frag) {
                FragmentActivity fragmentActivity = frag;
                switch (event) {
                    case YoyoEventLogin:
                        showLoginDialog();
                        break;
                    case YoyoEventExchange:
                        goExchange();
                        break;
                    case YoyoEventShare:
                        showShareInfo();
                        break;
                    default:
                        break;
                }
            }
        });

    }


    private void showDialogList() {
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        ListView listView = new ListView(this);//this为获取当前的上下文
        listView = new ListView(this);
        listView.setAdapter(simpleAdapter);
        listView.setFocusable(true);
        listView.setFadingEdgeLength(0);
        listView.setDivider(null);
        listView.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        linearLayoutMain.addView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YoyoApi.enterRoom(simpleAdapter.getList().get(position).getUid());
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("列表")
                .setView(linearLayoutMain).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showLoginDialog() {
        View loginView = LayoutInflater.from(this).inflate(R.layout.dialog_login, (ViewGroup) findViewById(R.id.login_root));
        final EditText editUser = (EditText) loginView.findViewById(R.id.login_user);
        final EditText editPw = (EditText) loginView.findViewById(R.id.login_password);
        final AlertDialog loginDialog = new AlertDialog.Builder(this).setTitle("登录框")
                .setView(loginView).setPositiveButton("登录", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String username = editUser.getText().toString();
                        String password = editPw.getText().toString();
                        AppHelper.doLogin(username, password);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                    }
                }).create();
        loginDialog.setCanceledOnTouchOutside(false);
        loginDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        loginDialog.show();
    }

    public void showShareInfo(){
        YoyoApi.getSharedData(new YoyoServerInterface<YoyoSharedRecord>() {
            @Override
            public boolean onOk(YoyoSharedRecord response) {
                if (response != null) {
                    yoyoSharedRecord = response;
                    showShareData();
                }
                return true;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                return true;
            }
        });
    }

    public void showUpdateDialog(){
        View updateView = LayoutInflater.from(this).inflate(R.layout.dialog_userinfo,(ViewGroup)findViewById(R.id.update_root));
        final EditText alias = (EditText) updateView.findViewById(R.id.edit_alias);
        final EditText avatar = (EditText) updateView.findViewById(R.id.edit_avatar);
        AlertDialog updateDialog = new AlertDialog.Builder(this).setTitle("修改用户信息")
                .setView(updateView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        YoyoApi.updateUserInfo(getMap(alias.getText().toString(), avatar.getText().toString())
                                , new YoyoServerInterface<Boolean>() {
                            @Override
                            public boolean onOk(Boolean response) {
                                if (response) {
                                    Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(YoyoErrorCode err, String msg) {
                                Toast.makeText(MainActivity.this, "update ->" + msg, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                    }
                }).create();
        updateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.show();
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

    public void showShareData() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("分享信息")
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

    private void showUserInfo(String openId, String name, String token, String amount){
        TextView im = (TextView) findViewById(R.id.userinfo);
        String content = "\n"+
                "openId : " + openId + "\n"+
                "name : " + name + "\n" +
                "token : " + token + "\n"+
                "amount : " + amount;

        im.setText(content);
    }

    void goLogin(){
        YoyoApi.loginYoyo(AppHelper.user.openId, AppHelper.user.token, new YoyoServerInterface<Boolean>() {
            @Override
            public boolean onOk(Boolean response) {
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                showUserInfo(AppHelper.user.openId, AppHelper.user.name, AppHelper.user.token, AppHelper.user.amount);
                return true;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void setAction(){
        AppHelper.setOk(new AppHelper.Ok() {
            @Override
            public void isOk(int tag) {
                if (tag == 1) {
                    goLogin();
                } else if (tag == 4) {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
