package net.moyokoo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.moyokoo.app.R;
import net.moyokoo.app.interfaces.DefaultPercentProgress;
import net.moyokoo.diooto.Diooto;
import net.moyokoo.diooto.config.DiootoConfig;
import net.moyokoo.diooto.config.ContentViewOriginModel;
import net.moyokoo.app.widget.NoScrollViewPager;
import net.moyokoo.app.interfaces.IIndicator;
import net.moyokoo.app.interfaces.IProgress;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private NoScrollViewPager mViewPager;
    List<ContentViewOriginModel> contentViewOriginModels;
    List<ImageFragment> fragmentList;
    DiootoConfig diootoConfig;
    FrameLayout indicatorLayout;
    static IIndicator iIndicator;
    static IProgress iProgress;
    boolean isNeedAnimationForClickPosition = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);
        iProgress = new DefaultPercentProgress();
        mViewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        diootoConfig = Diooto.getDiootoExtra(getIntent());
        //indicatorLayout.setVisibility(View.VISIBLE);
        int currentPosition = diootoConfig.getPosition();
        String[] imageUrls = diootoConfig.getImageUrls();
        contentViewOriginModels = diootoConfig.getContentViewOriginModels();
        fragmentList = new ArrayList<>();
        for (int i = 0; i < contentViewOriginModels.size(); i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(
                    imageUrls[i], i, diootoConfig.getType(),
                    contentViewOriginModels.size() == 1 || diootoConfig.getPosition() == i, contentViewOriginModels.get(i)
            );
            fragmentList.add(imageFragment);
        }
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        mViewPager.setCurrentItem(currentPosition);
        if (iIndicator != null && contentViewOriginModels.size() != 1) {
            iIndicator.attach(indicatorLayout);
            iIndicator.onShow(mViewPager);
        }
    }

    //用来判断第一次点击的时候是否需要动画  第一次需要动画  后续viewpager滑动回到该页面的时候  不做动画
    public boolean isNeedAnimationForClickPosition(int position) {
        return isNeedAnimationForClickPosition && diootoConfig.getPosition() == position;
    }

    public void refreshNeedAnimationForClickPosition() {
        isNeedAnimationForClickPosition = false;
    }

    public void finishView() {
        if (Diooto.onFinishListener != null) {
            Diooto.onFinishListener.finish(fragmentList.get(mViewPager.getCurrentItem()).getDragDiootoView());
        }
        Diooto.onLoadPhotoBeforeShowBigImageListener = null;
        Diooto.onShowToMaxFinishListener = null;
        Diooto.onProvideViewListener = null;
        Diooto.onFinishListener = null;
        iIndicator = null;
        iProgress = null;
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fragmentList.get(mViewPager.getCurrentItem()).backToMin();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
