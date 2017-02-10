package com.xcyo.simple;

import android.content.Context;
import android.util.Log;
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
    private static String TAG = "KsyVideoView";
    private KSYMediaPlayer mKsyMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private String mVideoUrl;

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {

        }
    };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.e(TAG, "onSeekComplete......");
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener(){
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            Log.e(TAG, "onVideoSizeChanged......");
            mKsyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (mListener != null) {
                mListener.onSizeChanged(mp.getVideoWidth(), mp.getVideoHeight());
            }
        }
    };

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Log.e(TAG, "onPrepared......");
            if (mKsyMediaPlayer != null){
                mKsyMediaPlayer.start();
            }
        }
    };

    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int i, int i1) {
            Log.e(TAG, "onInfo,code:" + i);
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.e(TAG, "开始缓冲数据");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.e(TAG, "数据缓冲完毕");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Log.e(TAG, "开始播放音频");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.e(TAG, "开始渲染视频");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD:
                    // 播放SDK有做快速开播的优化，在流的音视频数据交织并不好时，可能只找到某一个流的信息
                    // 当播放器读到另一个流的数据时会发出此消息通知
                    // 请务必调用reload接口
                    if(mKsyMediaPlayer != null){
                        reload(mVideoUrl);
                    }
                    break;
                case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                    Log.e(TAG, "reload成功的消息通知");
                    break;
            }
            return false;
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            Log.e(TAG, "onCompletion......");
            if (mListener != null) {
                mListener.onCompletion();
            }
        }
    };

    public IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int i, int i1) {
            Log.e(TAG, "onError,code:" + i);
            if (mListener != null){
                if (mListener.onError()){
                    return true;
                }
            }
            return false;
        }
    };

    public KSYVideoView(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        mKsyMediaPlayer = new KSYMediaPlayer.Builder(context).build();
        mKsyMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        mKsyMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        mKsyMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        mKsyMediaPlayer.setOnPreparedListener(mPreparedListener);
        mKsyMediaPlayer.setOnInfoListener(mInfoListener);
        mKsyMediaPlayer.setOnCompletionListener(mCompletionListener);
        mKsyMediaPlayer.setOnErrorListener(mErrorListener);

        mSurfaceView = new SurfaceView(context);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mKsyMediaPlayer != null) {
                    mKsyMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
                mKsyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mKsyMediaPlayer != null){
                    mKsyMediaPlayer.setDisplay(null);
                }
            }
        });
        return mSurfaceView;
    }

    @Override
    public void start(String url) {
        try {
            mKsyMediaPlayer.reset();
            mVideoUrl = url;
            mKsyMediaPlayer.setDataSource(url);
            mKsyMediaPlayer.prepareAsync();
            mKsyMediaPlayer.setDisplay(mSurfaceHolder);
            mKsyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload(String url) {
        mKsyMediaPlayer.reload(url,false,KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_ACCURATE);
    }

    @Override
    public void stop() {
        if (mKsyMediaPlayer != null){
            mKsyMediaPlayer.stop();
        }
    }

    @Override
    public String getVideoUrl() {
        return mVideoUrl;
    }

    @Override
    public void destory() {
        if (mKsyMediaPlayer != null){
            mKsyMediaPlayer.stop();
            mKsyMediaPlayer.release();
        }
        mKsyMediaPlayer = null;
        mSurfaceView = null;
    }
}
