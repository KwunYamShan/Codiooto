package net.moyokoo.diooto;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;

import net.moyokoo.diooto.config.ContentViewOriginModel;
import net.moyokoo.diooto.config.DiootoConfig;
import net.moyokoo.diooto.tools.Fucking;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;

public class Diooto {
    Context mContext;

    public static final String KEY_CONFIG = "config";

    public Diooto(Context context) {
        mContext = context;
        diootoConfig = new DiootoConfig();
    }

    private DiootoConfig diootoConfig;

    public static DiootoConfig getDiootoExtra(Intent intent) {
        if (intent != null) {
            return intent.getParcelableExtra(Diooto.KEY_CONFIG);
        }
        return null;
    }

    public Diooto urls(String imageUrl) {
        List<String> list = new ArrayList<>();
        list.add(imageUrl);
        this.diootoConfig.setImageUrls(list);
        return this;
    }

    public Diooto urls(List<String> imageUrls) {
        this.diootoConfig.setImageUrls(imageUrls);
        return this;
    }

    public Diooto originalUrls(List<String> originalUrls) {
        this.diootoConfig.setOriginalUrls(originalUrls);
        return this;
    }

    public Diooto type(int type) {
        this.diootoConfig.setType(type);
        return this;
    }

    /**
     * describe 设置视频的url.
     *
     * @author WuHao
     */
    public Diooto videoUrl(String videoUrl){
        this.diootoConfig.setVideoUrl(videoUrl);
        return  this;
    }

    /**
     * 是否需要退出动画
     * @param isAmin
     * @return
     */
    public Diooto isAnim(boolean isAmin){
        this.diootoConfig.setAnim(isAmin);
        return this;
    }

    /**
     * describe 点击的位置.
     */
    public Diooto position(int position) {
        return position(position, 0,0);
    }

    /**
     * describe 点击view的位置.
     *
     * @param position 当前点击view的位置
     * @param headerSize 头布局的数量
     * @param footerSize 脚布局的数量
     */
    public Diooto position(int position, int headerSize, int footerSize) {
        this.diootoConfig.setHeaderSize(headerSize);
        this.diootoConfig.setFooterSize(footerSize);
        this.diootoConfig.setPosition(position - headerSize);
        return this;
    }

    public Diooto views(View view) {
        return views(view,1);
    }

    /**
     * describe 同一个位置绑定多张图片.
     *
     * @author WuHao
     */
    public Diooto urlsBindView(List<String> urls, View view) {
        this.diootoConfig.setImageUrls(urls);
        if (urls!=null && urls.size()>0){
            views(view,urls.size());
        }
        return this;
    }

    /**
     * describe 可以传recylcerview自动识别(需要传在item布局中的viewId)  也可以手动传view数组.
     */
    public Diooto views(RecyclerView recyclerView, @IdRes int viewId) {
        List<View> originImageList = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View originImage = (recyclerView.getChildAt(i)
                    .findViewById(viewId));
            if (originImage != null) {
                originImageList.add(originImage);
            }
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPos = 0, lastPos = 0;
        int totalCount = layoutManager.getItemCount() ;
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayMan = (GridLayoutManager) layoutManager;
            firstPos = gridLayMan.findFirstVisibleItemPosition();
            lastPos = gridLayMan.findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linLayMan = (LinearLayoutManager) layoutManager;
            firstPos = linLayMan.findFirstVisibleItemPosition();
            lastPos = linLayMan.findLastVisibleItemPosition();
        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos);
        View[] views = new View[originImageList.size()];
        for (int i = 0; i < originImageList.size(); i++) {
            views[i] = originImageList.get(i);
        }
        return views(views);
    }

    /**
     * describe 每次回缩放到该view的位置上，注意totalCount需要和图片地址的数量一致.
     */
    public Diooto views(int totalCount, View view) {
        View[] views = new View[totalCount];
        for (int i = 0; i < totalCount; i++) {
            views[i] = view;
        }
        return views(views);
    }

    private void fillPlaceHolder(List<View> originImageList, int totalCount, int firstPos, int lastPos) {
        if (firstPos > 0) {
            for (int pos = firstPos; pos > 0; pos--) {
              if (diootoConfig.getHeaderSize()<pos) {
                originImageList.add(0, null);
              }
            }
        }
        if (lastPos < totalCount) {
            for (int i = (totalCount - 1 - lastPos); i > 0; i--) {
              if ( i > diootoConfig.getFooterSize()) {
                originImageList.add(null);
              }
            }
        }
    }

    public Diooto views(View[] views) {
        List<ContentViewOriginModel> list = new ArrayList<>();
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        int screenWidthCenter = screenWidth/2;
        int screenHeightCenter = screenHeight/2;
        for (View imageView : views) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            if (imageView == null) {
                imageBean.left = screenWidthCenter;
                imageBean.top = screenHeightCenter;
                imageBean.width = 0;
                imageBean.height = 0;
            } else {
                int location[] = new int[2];
                imageView.getLocationOnScreen(location);
                imageBean.left = location[0];
                imageBean.top = location[1] - Fucking.getFuckHeight(getWindow(imageView.getContext()));
                imageBean.width = imageView.getWidth();
                imageBean.height = imageView.getHeight();
            }
            list.add(imageBean);
        }
        diootoConfig.setContentViewOriginModels(list);
        return this;
    }


    public Diooto views(View view, int count) {
        List<ContentViewOriginModel> list = new ArrayList<>();
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        int screenWidthCenter = screenWidth/2;
        int screenHeightCenter = screenHeight/2;
        for (int i = 0 ;i<count ;i++) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            if (view == null) {
                imageBean.left = screenWidthCenter;
                imageBean.top = screenHeightCenter;
                imageBean.width = 0;
                imageBean.height = 0;
            } else {
                int location[] = new int[2];
                view.getLocationOnScreen(location);
                imageBean.left = location[0];
                imageBean.top = location[1] - Fucking.getFuckHeight(getWindow(view.getContext()));
                imageBean.width = view.getWidth();
                imageBean.height = view.getHeight();
            }
            list.add(imageBean);
        }
        diootoConfig.setContentViewOriginModels(list);
        return this;
    }

    /**
     * 给传入的class绑定数据并自动打开对应的activity
     *
     * @return
     */
    public Diooto start(Class clazz) {
        startImageActivity(clazz);
        return this;
    }

    /**
     * 需要调用者自行调用打开activity
     *  打开activity的方法参照startImageActivity(Class clazz)
     * @return
     */
    public Diooto start() {
        return this;
    }

    public DiootoConfig getConfig() {
        return diootoConfig;
    }

    Window getWindow(Context context) {
        if (getAppCompActivity(context) != null) {
            return getAppCompActivity(context).getWindow();
        } else {
            return scanForActivity(context).getWindow();
        }
    }

    Diooto startImageActivity(Class clazz) {
        Activity activity = scanForActivity(mContext);
        if (activity != null && clazz != null) {
            Intent intent = new Intent(activity, clazz);
            intent.putExtra(Diooto.KEY_CONFIG, getConfig());
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
        return this;
    }

    AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public Diooto loadPhotoBeforeShowBigImage(OnLoadPhotoBeforeShowBigImageListener on) {
        onLoadPhotoBeforeShowBigImageListener = on;
        return this;
    }

    public static OnLoadPhotoBeforeShowBigImageListener onLoadPhotoBeforeShowBigImageListener;

    public interface OnLoadPhotoBeforeShowBigImageListener {
        void loadView(SketchImageView sketchImageView, int position);
    }

    public static void cleanMemory(@NonNull Context context) {
        Sketch.with(context).getConfiguration().getDiskCache().clear();
        Sketch.with(context).getConfiguration().getBitmapPool().clear();
        Sketch.with(context).getConfiguration().getMemoryCache().clear();
    }
}
