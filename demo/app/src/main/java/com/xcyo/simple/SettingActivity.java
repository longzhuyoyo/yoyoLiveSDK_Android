package com.xcyo.simple;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.yoyo.video.YoyoBaseVideoView;
import com.xcyo.yoyo.video.YoyoSystemVideoView;

/**
 * Created by caixiaoxiao on 6/1/17.
 */
public class SettingActivity extends FragmentActivity{
    private int[] videoRBIDs = {
            R.id.main_video_view,
            R.id.main_ijkvideo_view,
            R.id.main_ksyvideo_view
    };
    private Class<? extends YoyoBaseVideoView>[] videoClsSet= new Class[]{
            YoyoSystemVideoView.class,
            YoyoIjkVideoView.class,
            KSYVideoView.class
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //设置播放器
        RadioGroup videoRG = (RadioGroup) findViewById(R.id.main_video_rg);
        videoRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < videoRBIDs.length;i++){
                    if (videoRBIDs[i] == checkedId){
                        YoyoApi.setMediaPlayerClazz(videoClsSet[i]);
                    }
                }
            }
        });

        Class cls = YoyoApi.getMediaPlayerClass();
        for (int i = 0; i < videoClsSet.length;i++){
            if (videoClsSet[i] == cls){
                videoRG.check(videoRBIDs[i]);
            }
        }

        //设置分享
        Switch shareSwitch = (Switch) findViewById(R.id.switch_set);
        shareSwitch.setChecked(YoyoApi.isShareAble());
        shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YoyoApi.setShareAble(isChecked);
            }
        });
    }
}
