package net.moyokoo.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.moyokoo.app.R;
import net.moyokoo.app.interfaces.CircleIndexIndicator;
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
    List<ImageFragment>          fragmentList;
    DiootoConfig                 diootoConfig;
    FrameLayout                  indicatorLayout;
    static IIndicator iIndicator;
    static IProgress  iProgress;
    boolean isNeedAnimationForClickPosition = true;
    private int currentItem;

    private RelativeLayout mRlTopBar;
    private TextView       mTvTitle;
    private String[]       mImageUrls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);
        initView();
        initData();
    }

    private void initView() {
        mRlTopBar = findViewById(R.id.rl_top_bar);
        mTvTitle = findViewById(R.id.tv_title);
        mViewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
    }

    public void initData() {
        iProgress = new DefaultPercentProgress();
        iIndicator = new CircleIndexIndicator();
        diootoConfig = Diooto.getDiootoExtra(getIntent());
        indicatorLayout.setVisibility(View.VISIBLE);
        mImageUrls = diootoConfig.getImageUrls();
        updateTitle();
        currentItem = diootoConfig.getPosition();
        contentViewOriginModels = diootoConfig.getContentViewOriginModels();
        boolean amin = diootoConfig.isAmin();
        fragmentList = new ArrayList<>();
        for (int i = 0; i < contentViewOriginModels.size(); i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(
                    mImageUrls[i], i, diootoConfig.getType(),
                    contentViewOriginModels.size() == 1 || diootoConfig.getPosition() == i, contentViewOriginModels.get(i), amin
            );
            fragmentList.add(imageFragment);
            imageFragment.setOnBackListener(new ImageFragment.OnBackListener() {
                @Override
                public void onBackToMin2() {
                    hiddenView(true);
                }

                @Override
                public void onBackToNormal2() {
                    hiddenView(false);
                }
            });
            imageFragment.setOnDragListener(new ImageFragment.OnDragListener() {
                @Override
                public void onDragListener() {
                    hiddenView(true);
                }
            });
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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                updateTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(currentItem);
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

    private void updateTitle() {
        mTvTitle.setText(getString(R.string.title, currentItem + 1, mImageUrls==null?0:mImageUrls.length));
    }

    public void hiddenView(boolean hidden) {
        if (hidden) {
            mRlTopBar.setVisibility(View.GONE);
        } else {
            mRlTopBar.setVisibility(View.VISIBLE);
        }
    }
}
