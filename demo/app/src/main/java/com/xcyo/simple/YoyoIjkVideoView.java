package com.xcyo.simple;

import android.content.Context;
import android.view.View;

import com.xcyo.yoyo.video.YoyoBaseVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.sample.widget.media.IjkVideoView;

/**
 * Created by caixiaoxiao on 8/10/16.
 */
public class YoyoIjkVideoView extends YoyoBaseVideoView{
    public String mVideoUrl;
    public YoyoIjkVideoView(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        IjkVideoView.Listener listener = new IjkVideoView.Listener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (mListener != null){
                    mListener.onCompletion();
                }
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                if (mListener != null){
                    mListener.onError();
                }
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                        if (mListener != null){
                            int videoWidth = iMediaPlayer.getVideoWidth();
                            int videoHeight = iMediaPlayer.getVideoHeight();
                            mListener.onSizeChanged(videoWidth,videoHeight);
                        }
                    }
                });

            }
        };
        IjkVideoView videoView = new IjkVideoView(context);
        videoView.setListener(listener);
        return videoView;
    }

    @Override
    public void start(String url) {
        mVideoUrl = url;
        ((IjkVideoView)mVideoView).rtmpStart(url,null);
    }

    @Override
    public void reload(String url) {
        ((IjkVideoView)mVideoView).rtmpStart(url,null);
    }

    @Override
    public void stop() {
        ((IjkVideoView)mVideoView).rtmpStop();
    }

    @Override
    public void destory() {
        IjkVideoView view = (IjkVideoView) mVideoView;
        view.stopPlayback();
        view.release(true);
        view.stopBackgroundPlay();
    }

    @Override
    public String getVideoUrl() {
        return mVideoUrl;
    }
}
