package net.moyokoo.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import net.moyokoo.app.R;

import org.salient.artplayer.AbsControlPanel;
import org.salient.artplayer.MediaPlayerManager;

public class SimpleControlPanel extends AbsControlPanel implements SeekBar.OnSeekBarChangeListener {

    private ProgressBar loading;
    private ImageView video_cover;
    private ImageView iv_play;
    public interface OnVideoPreparedListener{
        void prepared();
    }
    OnVideoPreparedListener onVideoPreparedListener;

    public void setOnVideoPreparedListener(OnVideoPreparedListener onVideoPreparedListener) {
        this.onVideoPreparedListener = onVideoPreparedListener;
    }

    public interface OnVideoPlayingListener{
        void playing();
    }
    OnVideoPlayingListener onVideoPlayingListener;

    public void setOnVideoPlayingListener(OnVideoPlayingListener onVideoPlayingListener) {
        this.onVideoPlayingListener = onVideoPlayingListener;
    }

    public interface OnPlayVideoClickListener{
        void onPlayClick();
    }
    OnPlayVideoClickListener onPlayVideoClickListener;

    public void setOnPlayVideoClickListener(OnPlayVideoClickListener onPlayVideoClickListener) {
        this.onPlayVideoClickListener = onPlayVideoClickListener;
    }


    public SimpleControlPanel(Context context) {
        super(context);
    }

    public SimpleControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected int getResourceId() {
        return R.layout.simple_control_panel;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(Context context) {
        super.init(context);
        loading = findViewById(R.id.loading);
        video_cover = findViewById(R.id.video_cover);
        iv_play = findViewById(R.id.iv_play);
        iv_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlayVideoClickListener!=null){
                    onPlayVideoClickListener.onPlayClick();
                }
            }
        });
    }



    @Override
    public void onStateError() {

    }

    @Override
    public void onStateIdle() {
        hideUI( loading);
        showUI(video_cover);
    }

    @Override
    public void onStatePreparing() {
        showUI(loading);
    }

    @Override
    public void onStatePrepared() {
        hideUI(loading);
        if (onVideoPreparedListener!=null){
            onVideoPreparedListener.prepared();
        }
    }

    @Override
    public void onStatePlaying() {
        if (onVideoPlayingListener!=null){
            onVideoPlayingListener.playing();
        }
    }

    @Override
    public void onStatePaused() {
    }

    @Override
    public void onStatePlaybackCompleted() {
        showUI(iv_play);

    }

    @Override
    public void onSeekComplete() {
        hideUI(iv_play);
    }

    @Override
    public void onBufferingUpdate(int progress) {
    }

    @Override
    public void onInfo(int what, int extra) {
    }

    @Override
    public void onProgressUpdate(final int progress, final long position, final long duration) {
    }

    @Override
    public void onEnterSecondScreen() {
    }

    @Override
    public void onExitSecondScreen() {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        MediaPlayerManager.instance().cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MediaPlayerManager.instance().startProgressTimer();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PLAYING &&
            MediaPlayerManager.instance().getPlayerState() != MediaPlayerManager.PlayerState.PAUSED)
            return;
        long time = (long) (seekBar.getProgress() * 1.00 / 100 * MediaPlayerManager.instance().getDuration());
        MediaPlayerManager.instance().seekTo(time);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            long duration = MediaPlayerManager.instance().getDuration();
        }
    }

    //显示WiFi状态提醒
    public void showWifiAlert() {
        hideUI(loading);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}
