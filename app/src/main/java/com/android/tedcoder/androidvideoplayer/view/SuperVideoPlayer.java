package com.android.tedcoder.androidvideoplayer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.android.tedcoder.androidvideoplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ted on 2015/8/6.
 */
public class SuperVideoPlayer extends RelativeLayout {
    private final int TIME_SHOW_CONTROLLER = 3000;
    private final int TIME_UPDATE_PLAY_TIME = 1000;

    private final int MSG_HIDE_CONTROLLER = 10;
    private final int MSG_UPDATE_PLAY_TIME = 11;


    private Context mContext;
    private SuperVideoView mSuperVideoView;
    private MediaController mMediaController;
    private View mProgressBarView;
    private View mCloseBtnView;
    private Timer mUpdateTimer;
    private VideoPlayCallbackImpl mVideoPlayCallback;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPDATE_PLAY_TIME) {
                updatePlayTime();
                updatePlayProgress();
            } else if (msg.what == MSG_HIDE_CONTROLLER) {
                showOrHideController();
            }
        }
    };

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.video_close) {
                mVideoPlayCallback.onCloseVideo();
            }
        }
    };

    private View.OnTouchListener mOnTouchVideoListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                showOrHideController();
            }
            return false;
        }
    };

    private MediaController.MediaControlImpl mMediaControl = new MediaController.MediaControlImpl() {
        @Override
        public void onPlayTurn() {
            if (mSuperVideoView.isPlaying()) {
                pausePlay();
            } else {
                startPlayVideo();
            }
        }

        @Override
        public void onPageTurn() {
            mVideoPlayCallback.onSwitchPageType();
        }

        @Override
        public void onProgressTurn(MediaController.ProgressState state, int progress) {
            if (state.equals(MediaController.ProgressState.START)) {
                mHandler.removeMessages(MSG_HIDE_CONTROLLER);
            } else if (state.equals(MediaController.ProgressState.STOP)) {
                resetHideTimer();
            } else {
                int time = progress * mSuperVideoView.getDuration() / 100;
                mSuperVideoView.seekTo(time);
                updatePlayTime();
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mSuperVideoView.setVideoWidth(mediaPlayer.getVideoWidth());
            mSuperVideoView.setVideoHeight(mediaPlayer.getVideoHeight());
            mCloseBtnView.setVisibility(VISIBLE);
            mProgressBarView.setVisibility(GONE);
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            stopUpdateTimer();
            stopHideTimer();
            mMediaController.playFinish(mSuperVideoView.getDuration());
        }
    };

    public SuperVideoView getSuperVideoView() {
        return mSuperVideoView;
    }

    public void setPageType(MediaController.PageType pageType){
        mMediaController.setPageType(pageType);
    }

    /***
     * 加载视频
     * @param videoUrl
     */
    public void loadVideo(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            Log.e("TAG", "videoUrl should not be null");
            return;
        }
        resetUpdateTimer();
        mSuperVideoView.setOnPreparedListener(mOnPreparedListener);
        mSuperVideoView.setVideoPath(videoUrl);
    }

    /***
     * 加载并开始播放视频
     * @param videoUrl
     */
    public void loadAndPlay(String videoUrl) {
        loadVideo(videoUrl);
        startPlayVideo();
    }

    /**
     * 播放视频
     * should called after loadVideo()
     */
    public void startPlayVideo() {
        if (null == mUpdateTimer) resetUpdateTimer();
        resetHideTimer();
        mSuperVideoView.setOnCompletionListener(mOnCompletionListener);
        mSuperVideoView.start();
        mMediaController.setPlayState(MediaController.PlayState.PLAY);
    }

    public void setVideoPlayCallback(VideoPlayCallbackImpl videoPlayCallback) {
        mVideoPlayCallback = videoPlayCallback;
    }

    public void pausePlay() {
        mSuperVideoView.pause();
        mMediaController.setPlayState(MediaController.PlayState.PAUSE);
        stopHideTimer();
    }

    public void stopPlay() {
        pausePlay();
        stopUpdateTimer();
    }

    public SuperVideoPlayer(Context context) {
        super(context);
        initView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View.inflate(context,R.layout.super_vodeo_player_layout,this);
        mSuperVideoView = (SuperVideoView)findViewById(R.id.video_view);
        mMediaController = (MediaController)findViewById(R.id.controller);
        mProgressBarView = findViewById(R.id.progressbar);
        mCloseBtnView = findViewById(R.id.video_close);

        mMediaController.setMediaControl(mMediaControl);
        mSuperVideoView.setOnTouchListener(mOnTouchVideoListener);

        mCloseBtnView.setVisibility(GONE);
        mCloseBtnView.setOnClickListener(mOnClickListener);
        mProgressBarView.setVisibility(VISIBLE);
    }

    private void updatePlayTime() {
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition();
        mMediaController.setPlayProgressTxt(playTime, allTime);
    }

    private void updatePlayProgress() {
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition();
        int loadProgress = mSuperVideoView.getBufferPercentage();
        int progress = playTime * 100 / allTime;
        mMediaController.setProgressBar(progress, loadProgress);
    }

    private void showOrHideController() {
        if (mMediaController.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_exit_from_bottom);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mMediaController.setVisibility(View.GONE);
                }
            });
            mMediaController.startAnimation(animation);
        } else {
            mMediaController.setVisibility(View.VISIBLE);
            mMediaController.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_enter_from_bottom);
            mMediaController.startAnimation(animation);
            resetHideTimer();
        }
    }

    private void resetHideTimer() {
        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, TIME_SHOW_CONTROLLER);
    }

    private void stopHideTimer() {
        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
        mMediaController.setVisibility(View.VISIBLE);
        mMediaController.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_enter_from_bottom);
        mMediaController.startAnimation(animation);
    }

    private void resetUpdateTimer() {
        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
            }
        }, 0, TIME_UPDATE_PLAY_TIME);
    }

    private void stopUpdateTimer() {
        mUpdateTimer.cancel();
        mUpdateTimer = null;
    }

    private class AnimationImp implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    public interface VideoPlayCallbackImpl{
        void onCloseVideo();
        void onSwitchPageType();
    }
}
