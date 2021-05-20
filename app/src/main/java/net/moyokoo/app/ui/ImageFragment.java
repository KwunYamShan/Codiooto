package net.moyokoo.app.ui;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.moyokoo.app.R;
import net.moyokoo.app.widget.SimpleControlPanel;
import net.moyokoo.app.widget.SimpleControlPanel.OnPlayVideoClickListener;
import net.moyokoo.diooto.Diooto;
import net.moyokoo.diooto.DragDiootoView;
import net.moyokoo.diooto.config.DiootoConfig;
import net.moyokoo.diooto.config.ContentViewOriginModel;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayListener;
import me.panpf.sketch.request.DownloadProgressListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.request.LoadResult;
import me.panpf.sketch.util.SketchUtils;
import org.salient.artplayer.VideoView;

public class ImageFragment extends Fragment {
    DragDiootoView         dragDiootoView;
    ContentViewOriginModel contentViewOriginModel;
    String                 url;
    String                 mVideoUrl;
    SketchImageView        sketchImageView;
    int                    position;
    int                    type                = DiootoConfig.PHOTO;
    FrameLayout            loadingLayout;
    boolean                shouldShowAnimation = false;
    boolean                hasCache;
    private boolean isAnim;

    public DragDiootoView getDragDiootoView() {
        return dragDiootoView;
    }

    public static ImageFragment newInstance(String url, int position, int type, boolean shouldShowAnimation, ContentViewOriginModel contentViewOriginModel, boolean isAmin,String videoUrl) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("videoUrl", videoUrl);
        args.putInt("position", position);
        args.putBoolean("shouldShowAnimation", shouldShowAnimation);
        args.putInt("type", type);
        args.putParcelable("model", contentViewOriginModel);
        args.putBoolean("isAnim", isAmin);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            mVideoUrl = getArguments().getString("videoUrl");
            position = getArguments().getInt("position");
            shouldShowAnimation = getArguments().getBoolean("shouldShowAnimation");
            type = getArguments().getInt("type");
            isAnim = getArguments().getBoolean("isAnim");
            contentViewOriginModel = getArguments().getParcelable("model");
        }
        loadingLayout = view.findViewById(R.id.loadingLayout);
        dragDiootoView = view.findViewById(R.id.dragDiootoView);
        dragDiootoView.setPhoto(!isHaveVideo());
        dragDiootoView.setAnim(isAnim);
        if (ImageActivity.iProgress != null) {
            ImageActivity.iProgress.attach(position, loadingLayout);
        }
        loadingLayout.setVisibility(View.GONE);
        if (isHaveVideo()) {
            if (dragDiootoView.getContentParentView().getChildCount() <= 0) {
                dragDiootoView.addContentChildView(new VideoView(getActivity()));

                sketchImageView = new SketchImageView(getContext());
                sketchImageView.getOptions().setDecodeGifImage(true)
                    .setErrorImage(R.drawable.ic_error);
                sketchImageView.setZoomEnabled(true);
                dragDiootoView.addContentChildView(sketchImageView);
                sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser());
            }
        } else {
            sketchImageView = new SketchImageView(getContext());
            sketchImageView.getOptions().setDecodeGifImage(true) .setErrorImage(R.drawable.ic_error);;
            sketchImageView.setZoomEnabled(true);
            dragDiootoView.addContentChildView(sketchImageView,false);
            sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser());
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null || getActivity() == null) {
            return;
        }
        if (Diooto.onLoadPhotoBeforeShowBigImageListener != null) {
            if (dragDiootoView.getContentView() instanceof SketchImageView) {
                Diooto.onLoadPhotoBeforeShowBigImageListener.loadView((SketchImageView) dragDiootoView.getContentView(), position);
            } else if (dragDiootoView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                Diooto.onLoadPhotoBeforeShowBigImageListener.loadView((SketchImageView) dragDiootoView.getContentParentView().getChildAt(1), 0);
                dragDiootoView.getContentParentView().getChildAt(1).setVisibility(View.VISIBLE);
            }
        }else{
            if (dragDiootoView.getContentView() instanceof SketchImageView) {
                ((SketchImageView)dragDiootoView.getContentView()).displayImage(url);
            } else if (dragDiootoView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                ((SketchImageView)dragDiootoView.getContentParentView().getChildAt(1)).displayImage(url);
                ((SketchImageView)dragDiootoView.getContentParentView().getChildAt(1)).setVisibility(View.VISIBLE);
            }
        }
        dragDiootoView.setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
            @Override
            public void showFinish(DragDiootoView view, boolean showImmediately) {
                if (isHaveVideo()) {
                    loadImage();
                    loadingLayout.setVisibility(View.VISIBLE);
                    if (ImageActivity.iProgress != null) {
                        ImageActivity.iProgress.onStart(position);
                    }
                    VideoView videoView = (VideoView) dragDiootoView.getContentView();
                    SimpleControlPanel simpleControlPanel = new SimpleControlPanel(getContext());
                    simpleControlPanel.setOnClickListener(v2 -> dragDiootoView.backToMin());
                    simpleControlPanel.setOnVideoPreparedListener(() -> {
                        ((SketchImageView) dragDiootoView.getContentParentView().getChildAt(1))
                            .setVisibility(View.GONE);
                        if (ImageActivity.iProgress !=null && ImageActivity.iProgress.getProgressView(position) != null) {
                            (ImageActivity.iProgress.getProgressView(position)).setVisibility(View.GONE);
                        }
                    });
                    simpleControlPanel.setOnVideoPlayingListener(() -> {
                        //后台或者视频没有显示再页面上时暂停视频
                        if (!getUserVisibleHint() || !isResumed()) {
                            pauseVideo();
                        }
                    });
                    simpleControlPanel.setOnPlayVideoClickListener(new OnPlayVideoClickListener() {
                        @Override
                        public void onPlayClick() {
                            startVideo();
                        }
                    });
                    videoView.setControlPanel(simpleControlPanel);
                    videoView.setUp(mVideoUrl);
                    if (getUserVisibleHint()) {
                        startVideo();
                    }
                } else if (type == DiootoConfig.PHOTO && view.getContentView() instanceof SketchImageView && !hasCache) {
                    loadImage();
                }
            }
        });
        dragDiootoView.setOnDragListener(new DragDiootoView.OnDragListener() {
            @Override
            public void onDrag(DragDiootoView view, float moveX, float moveY) {
                if (ImageActivity.iIndicator != null) {
                    ImageActivity.iIndicator.move(moveX, moveY);
                }
                if (mOnDragListener != null) {
                    mOnDragListener.onDragListener();
                }
            }
        });

        dragDiootoView.setOnBackToMinListener(new DragDiootoView.OnBackToMinListener() {
            @Override
            public void onBackToMin() {
                if (mOnBackListener != null) {
                    mOnBackListener.onBackToMin2();
                }
            }
        });
        dragDiootoView.setOnBackToNormalListener(new DragDiootoView.OnBackToNormalListener() {
            @Override
            public void onBackToNormal() {
                if (mOnBackListener != null) {
                    mOnBackListener.onBackToNormal2();
                }
            }
        });
        DiskCache diskCache = Sketch.with(getContext()).getConfiguration().getDiskCache();
        hasCache = type == DiootoConfig.PHOTO && !((ImageActivity) getActivity()).isNeedAnimationForClickPosition(position) && diskCache.exist(url)  && !isHaveVideo();
        if (hasCache) {
            ((ImageActivity) getActivity()).refreshNeedAnimationForClickPosition();
            loadImage();
        } else {
            dragDiootoView.putData(contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(), contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight());
            //如果显示的点击的position  则进行动画处理
            dragDiootoView.show(!shouldShowAnimation);
        }
        dragDiootoView.setOnFinishListener(new DragDiootoView.OnFinishListener() {
            @Override
            public void callFinish() {
                if (getContext() instanceof ImageActivity) {
                    ((ImageActivity) getContext()).finishView();
                }
            }
        });
        dragDiootoView.setOnReleaseListener(new DragDiootoView.OnReleaseListener() {
            @Override
            public void onRelease(boolean isToMax, boolean isToMin) {
                if (ImageActivity.iIndicator != null) {
                    ImageActivity.iIndicator.fingerRelease(isToMax, isToMin);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dragDiootoView.notifySizeConfig();
    }


    /**
     * 由于图片框架原因  这里需要使用两种不同的加载方式
     * 如果有缓存  直接可显示
     * 如果没缓存 则需要等待加载完毕  才能够将图片显示在view上
     */
    private void loadImage() {
        if (getContext() == null || sketchImageView == null) {
            return;
        }
        if (hasCache) {
            loadWithCache();
        } else {
            loadWithoutCache();
        }
    }

    private void loadWithCache() {
        sketchImageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                loadingLayout.setVisibility(View.VISIBLE);
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onStart(position);
                }
            }

            @Override
            public void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
                loadingLayout.setVisibility(View.GONE);
                if (ImageActivity.iProgress != null && !isHaveVideo()) {
                    ImageActivity.iProgress.onFinish(position);
                }
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                //如果有缓存  直接将大小变为最终大小而不是去调用notifySize来更新 并且是直接显示不进行动画
                dragDiootoView.putData(
                        contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(),
                        contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight(),
                        w, h);
                dragDiootoView.show(true);
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onFailed(position);
                }
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {

            }
        });
        sketchImageView.setDownloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                loadingLayout.setVisibility(View.VISIBLE);
                int ratio = (int) (completedLength / (float) totalLength * 100);
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onProgress(position, ratio);
                }
            }
        });
        sketchImageView.displayImage(url);
    }

    LoadRequest loadRequest;

    private void loadWithoutCache() {
        loadRequest = Sketch.with(getContext()).load(url, new LoadListener() {
            @Override
            public void onStarted() {
                loadingLayout.setVisibility(View.VISIBLE);
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onStart(position);
                }
            }

            @Override
            public void onCompleted(@NonNull LoadResult result) {
                loadingLayout.setVisibility(View.GONE);
                if (ImageActivity.iProgress != null && !isHaveVideo()) {
                    ImageActivity.iProgress.onFinish(position);
                }
                if (result.getGifDrawable() != null) {
                    result.getGifDrawable().followPageVisible(true, true);
                }
                int w = result.getBitmap().getWidth();
                int h = result.getBitmap().getHeight();
                dragDiootoView.notifySize(w, h);
                sketchImageView.displayImage(url);
                hasCache = true;
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onFailed(position);
                }
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
            }
        }).downloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                loadingLayout.setVisibility(View.VISIBLE);
                int ratio = (int) (completedLength / (float) totalLength * 100);
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onProgress(position, ratio);
                }
            }
        }).commit();
    }

    @Override
    public void onDestroyView() {
        if (loadRequest != null) {
            loadRequest.cancel(CancelCause.ON_DETACHED_FROM_WINDOW);
            loadRequest = null;
        }
        super.onDestroyView();
    }

    public void backToMin() {
        dragDiootoView.backToMin();
    }

    /**
     * SketchImageView 生命周期处理
     */

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
        pauseVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
            startVideo();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(isVisibleToUser);
        }
        if (isVisibleToUser) {
            startVideo();
        } else {
            pauseVideo();
        }
    }

    public void startVideo(){
        if (isHaveVideo() && getVideoView() != null) {
            getVideoView().start();
        }
    }

    public void pauseVideo(){
        if (isHaveVideo() && getVideoView() != null) {
            getVideoView().pause();
        }
    }

    public VideoView getVideoView() {
        if (dragDiootoView != null && dragDiootoView.getContentView() instanceof VideoView) {
            VideoView videoView = (VideoView) dragDiootoView.getContentView();
            return videoView;
        }
        return null;
    }


    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        // 不可见的时候暂停分块显示器，节省内存，可见的时候恢复
        if (sketchImageView != null && sketchImageView.isZoomEnabled()) {
            sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser);
            Drawable lastDrawable = SketchUtils.getLastDrawable(sketchImageView.getDrawable());
            if (lastDrawable != null && (lastDrawable instanceof SketchGifDrawable)) {
                ((SketchGifDrawable) lastDrawable).followPageVisible(isVisibleToUser, false);
            }
        }
    }

    public interface OnBackListener {
        void onBackToMin2();

        void onBackToNormal2();
    }

    private OnBackListener mOnBackListener;
    public void setOnBackListener(OnBackListener listener) {
        mOnBackListener = listener;
    }

    public interface OnDragListener {
        void onDragListener();
    }

    private OnDragListener mOnDragListener;
    public void setOnDragListener(OnDragListener listener) {
        mOnDragListener = listener;
    }


    /**
     * describe 判断是否有视频.
     *
     * @author WuHao
     */
    public boolean isHaveVideo() {
        if (position == 0 && !TextUtils.isEmpty(mVideoUrl)) {
            return true;
        }
        return false;
    }
}
