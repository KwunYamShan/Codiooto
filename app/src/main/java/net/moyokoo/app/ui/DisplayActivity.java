package net.moyokoo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

import net.moyokoo.app.R;
import net.moyokoo.app.widget.SimpleControlPanel;
import net.moyokoo.app.widget.WrapRecyclerView;
import net.moyokoo.diooto.config.DiootoConfig;
import net.moyokoo.diooto.Diooto;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.ScaleType;
import org.salient.artplayer.VideoView;

import me.panpf.sketch.SketchImageView;

public class DisplayActivity extends AppCompatActivity {
    WrapRecyclerView mRecyclerView;
    String[]         longImageUrl   = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "http://wx3.sinaimg.cn/large/9f780829ly1fwvwhq9cg3j2cn40e2npj.jpg",
            "https://wx2.sinaimg.cn/mw600/6d239c49ly1fwsvs7rtocj20k3cmpkjs.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg"
    };
    String[] normalImageUlr = new String[]{
            "http://img1.juimg.com/140908/330608-140ZP1531651.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4252693316,3220141270&fm=26&gp=0.jpg",
            "123",
            "https://github.com/moyokoo/Media/blob/master/diooto4.jpg?raw=true",
            "https://github.com/moyokoo/Media/blob/master/diooto5.jpg?raw=true",
            "https://github.com/moyokoo/Media/blob/master/diooto6.jpg?raw=true",
            "https://github.com/moyokoo/Media/blob/master/diooto7.png?raw=true",
            "https://github.com/moyokoo/Media/blob/master/diooto8.png?raw=true",
            "https://github.com/moyokoo/Media/blob/master/diooto9.jpg?raw=true",
    };
    Context          context;
    int              activityPosition;
    boolean          isImmersive    = true;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_display);

        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ImageAdapter(this, normalImageUlr));

        ImmersionBar.with(this).init();
        activityPosition = getIntent().getIntExtra("position", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Diooto");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new MainAdapter());
        mRecyclerView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_header, null));
        mRecyclerView.addFooterView(LayoutInflater.from(this).inflate(R.layout.adapter_footer, null));

    }

    public static void newIntent(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, DisplayActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = null;
            View view = LayoutInflater.from(
                    DisplayActivity.this).inflate(R.layout.item_grid, parent,
                    false);
            int size = getResources().getDisplayMetrics().widthPixels / 3 - 16;
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
            int padding = 16;
            lp.setMargins(padding, padding, padding, padding);
            view.setLayoutParams(lp);
            holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.srcImageView.displayImage(normalImageUlr[position]);
            holder.srcImageView.setShowGifFlagEnabled(R.drawable.ic_gif);
            holder.srcImageView.setOnClickListener(srcView -> {

                int size = mRecyclerView.getChildCount();
                View[] views = new View[size];
                int[] realWidths = new int[size];
                int[] realHeights = new int[size];
                for (int i = 0; i < size; i++) {
                    ImageView recyImageView = mRecyclerView.getChildAt(i).findViewById(R.id.srcImageView);
                    views[i] = recyImageView;
                    realWidths[i] = 1920;
                    realHeights[i] = 720;
                }
                if (activityPosition == 3) {
                    //加载视频
                    Diooto diooto = new Diooto(context)
                            .urls(normalImageUlr[position])
                            .position(holder.getAdapterPosition())
                            .views(holder.srcImageView)
                            .type(DiootoConfig.VIDEO)
                            .isAnim(true)
                            .immersive(isImmersive)
                            //提供视频View
                            .onProvideVideoView(() -> new VideoView(context))
                            //显示视频加载之前的缩略图
                            .loadPhotoBeforeShowBigImage((sketchImageView, position13) -> sketchImageView.displayImage(normalImageUlr[position]))
                            //动画到最大化时的接口
                            .onVideoLoadEnd((dragDiootoView, sketchImageView, progressView) -> {
                                VideoView videoView = (VideoView) dragDiootoView.getContentView();
                                SimpleControlPanel simpleControlPanel = new SimpleControlPanel(context);
                                simpleControlPanel.setOnClickListener(v -> dragDiootoView.backToMin());
                                simpleControlPanel.setOnVideoPreparedListener(() -> {
                                    sketchImageView.setVisibility(View.GONE);
                                    progressView.setVisibility(View.GONE);
                                });
                                videoView.setControlPanel(simpleControlPanel);
                                //videoView.setUp("http://bmob-cdn-982.b0.upaiyun.com/2017/02/23/266454624066f2b680707492a0664a97.mp4");
                                videoView.setUp("https://vdse.bdstatic.com//28df11aa5252020ace6fa4321f5a50e3.mp4?authorization=bce-auth-v1/fb297a5cc0fb434c971b8fa103e8dd7b/2017-05-11T09:02:31Z/-1//b3d16a3d534465108ca76bf89d90f86e5b1be6543119a9d864b6d3c315251725");
                                videoView.start();
                                dragDiootoView.notifySize(1920, 1080);
                                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
                            })
                            //到最小状态的接口
                            .onFinish(dragDiootoView -> MediaPlayerManager.instance().releasePlayerAndView(context))
                            .start(ImageActivity.class);
                } else if (activityPosition == 1) {
                    //加载单张图片
                    Diooto diooto = new Diooto(context)
                            .urls(normalImageUlr[position])
                            .type(DiootoConfig.PHOTO)
                            .immersive(isImmersive)
                            .position(0)
                            .isAnim(true)
                            .views(views[holder.getAdapterPosition()])
                            .loadPhotoBeforeShowBigImage((sketchImageView, position1) -> {
                                sketchImageView.displayImage(normalImageUlr[position]);
                            })
                            .start(ImageActivity.class);
                } else {
                    Diooto diooto = new Diooto(context)
                            .urls(activityPosition == 2 ? longImageUrl : normalImageUlr)
                            .type(DiootoConfig.PHOTO)
                            .immersive(isImmersive)
                            .isAnim(true)
                            .position(holder.getAdapterPosition(),1,1)
                            .views(mRecyclerView, R.id.srcImageView)
                            .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> {
                                sketchImageView.displayImage(activityPosition == 2 ? longImageUrl[position] : normalImageUlr[position]);
                                sketchImageView.setOnLongClickListener(v -> {
                                    Toast.makeText(DisplayActivity.this, "Long click", Toast.LENGTH_SHORT).show();
                                    return false;
                                });
                            })
                            .start(ImageActivity.class);

                }
            });
        }

        @Override
        public int getItemCount() {
            return normalImageUlr.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            SketchImageView srcImageView;

            public MyViewHolder(View view) {
                super(view);
                srcImageView = view.findViewById(R.id.srcImageView);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }


    public class ImageAdapter extends PagerAdapter {

        private final String[] mList;
        private final Context  mContext;

        public ImageAdapter(Context context, String[] list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {


            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int index = position % mList.length;
            View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);
            SketchImageView imageView = convertView.findViewById(R.id.imageView);
            imageView.displayImage(mList[index]);
            container.addView(convertView);
            convertView.setOnClickListener(v -> {
                Diooto diooto = new Diooto(mContext)
                        .urls(mList)
                        .type(DiootoConfig.PHOTO)
                        .immersive(true)
                        .isAnim(false)
                        .position(index)
                        .views(mList.length, imageView)
                        .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> {
                            sketchImageView.displayImage(mList[index]);
                            sketchImageView.setOnLongClickListener(v2 -> {
                                Toast.makeText(DisplayActivity.this, "Long click", Toast.LENGTH_SHORT).show();
                                return false;
                            });
                        })
                        .start(ImageActivity.class);
            });

            return convertView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
