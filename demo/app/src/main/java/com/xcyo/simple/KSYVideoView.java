package com.xcyo.simple;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.xcyo.yoyo.video.YoyoBaseVideoView;

import java.io.IOException;

/**
 * Created by caixiaoxiao on 19/10/16.
 */
public class KSYVideoView extends YoyoBaseVideoView{
    private KSYMediaPlayer mKsyMediaPlayer;
    public KSYVideoView(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        mKsyMediaPlayer = new KSYMediaPlayer.Builder(context).build();
        //监听播放器的大小变化
        mKsyMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                if (mListener != null) {
                    mListener.onSizeChanged(iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight());
                }
            }
        });
        mKsyMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mKsyMediaPlayer.start();
            }
        });
        //监听播放器的完成
        mKsyMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mListener.onCompletion();
            }
        });
        //监听播放错误
        mKsyMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mListener != null){
                    mListener.onError();
                }
                return false;
            }
        });
        SurfaceView surfaceView = new SurfaceView(context);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mKsyMediaPlayer != null){
                    mKsyMediaPlayer.setDisplay(holder);
                    mKsyMediaPlayer.setScreenOnWhilePlaying(true);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mKsyMediaPlayer != null){
                    mKsyMediaPlayer.setDisplay(null);
                }
            }
        };
        surfaceHolder.addCallback(surfaceCallback);
        return surfaceView;
    }

    @Override
    public void start(String url) {
        if (mKsyMediaPlayer != null){
            try {
                mKsyMediaPlayer.setDataSource(url);
                mKsyMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (mKsyMediaPlayer != null){
            mKsyMediaPlayer.stop();
        }
    }

    @Override
    public void destory() {
        if (mKsyMediaPlayer != null){
            mKsyMediaPlayer.release();
        }
        mKsyMediaPlayer = null;
        mVideoView = null;
    }
}
