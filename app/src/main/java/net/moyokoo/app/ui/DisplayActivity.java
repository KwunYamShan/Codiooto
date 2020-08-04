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

import java.util.Arrays;
import java.util.List;
import net.moyokoo.app.R;
import net.moyokoo.app.widget.WrapRecyclerView;
import net.moyokoo.diooto.Diooto;

import org.salient.artplayer.MediaPlayerManager;

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
  String[]         normalImageUlr = new String[]{
      "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2556516854,156454147&fm=26&gp=0.jpg",
      "http://img1.juimg.com/140908/330608-140ZP1531651.jpg",
      "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4252693316,3220141270&fm=26&gp=0.jpg",
      "https://img.hbhcdn.com/zhuanti/20890/zhanwei.jpg",
      "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2968785817,771434359&fm=26&gp=0.jpg",
      "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1481652703,2245130276&fm=26&gp=0.jpg",
      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1596083241906&di=ecafe0055d058a8df6141f628b85d00d&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fw%3D580%2Fsign%3D2f1242b2a344ad342ebf878fe0a30c08%2Fdec7157adab44aed90425ebbb11c8701a08bfb45.jpg",

      "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3819244488,33241013&fm=26&gp=0.jpg",
      "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2801246744,2616381601&fm=26&gp=0.jpg",
  };

  String [] videoUrl = new String[]{
      "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
      "https://vdse.bdstatic.com//28df11aa5252020ace6fa4321f5a50e3.mp4?authorization=bce-auth-v1/fb297a5cc0fb434c971b8fa103e8dd7b/2017-05-11T09:02:31Z/-1//b3d16a3d534465108ca76bf89d90f86e5b1be6543119a9d864b6d3c315251725",
      "https://v-cdn.zjol.com.cn/280443.mp4",
      "https://vod.hbhcdn.com/dmp/test/b/lpmicrofilm/43742240577949696.mp4"
  };
  Context          context;
  int              activityPosition;
  boolean          isImmersive    = true;
  private ViewPager    mViewPager;
  private List<String> mNormalImageUlrList;
  private List<String> mLongImageUlrList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    setContentView(R.layout.activity_display);
    initDate();
    mViewPager = findViewById(R.id.viewpager);
    mViewPager.setAdapter(new ImageAdapter(this, mNormalImageUlrList));

    ImmersionBar.with(this).init();
    activityPosition = getIntent().getIntExtra("position", 0);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Diooto");
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
          actionBar.setDisplayHomeAsUpEnabled(true);
      }
    mRecyclerView = findViewById(R.id.recyclerView);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    mRecyclerView.setAdapter(new MainAdapter());
    mRecyclerView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_header, null));
    mRecyclerView.addFooterView(LayoutInflater.from(this).inflate(R.layout.adapter_footer, null));

  }

  private void initDate() {
    mNormalImageUlrList = Arrays.asList(normalImageUlr);
    mLongImageUlrList = Arrays.asList(longImageUrl);
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
          //图片视频混合
          Diooto diooto = new Diooto(context)
              .position(holder.getAdapterPosition(),1,1)
              .urlsBindView(mNormalImageUlrList, holder.srcImageView)
              .isAnim(true)
              .videoUrl("https://vdse.bdstatic.com//28df11aa5252020ace6fa4321f5a50e3.mp4?authorization=bce-auth-v1/fb297a5cc0fb434c971b8fa103e8dd7b/2017-05-11T09:02:31Z/-1//b3d16a3d534465108ca76bf89d90f86e5b1be6543119a9d864b6d3c315251725")
              //提供视频View
              .start(ImageActivity.class);
        } else if (activityPosition == 1) {
          //加载单张图片
          Diooto diooto = new Diooto(context)
              .urls(mNormalImageUlrList.get(position))
              .isAnim(true)
              .views(holder.srcImageView)
              .start(ImageActivity.class);
        } else {
          //图片
          Diooto diooto = new Diooto(context)
              .urls(activityPosition == 2 ? mLongImageUlrList : mNormalImageUlrList)
              .isAnim(true)
              .position(holder.getAdapterPosition(), 1, 1)
              .views(mRecyclerView, R.id.srcImageView)
              .start(ImageActivity.class);

        }
      });
    }

    @Override
    public int getItemCount() {
      return mNormalImageUlrList.size();
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

    private final List<String> mList;
    private final Context  mContext;

    public ImageAdapter(Context context, List<String> list) {
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
      int index = position % mList.size();
      View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);
      SketchImageView imageView = convertView.findViewById(R.id.imageView);
      imageView.displayImage(mList.get(index));
      container.addView(convertView);
      convertView.setOnClickListener(v -> {
        Diooto diooto = new Diooto(mContext)
            .urls(mList)
            .isAnim(false)
            .position(index)
            .views(mList.size(), imageView)
            .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> {
              sketchImageView.displayImage(mList.get(index));
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
