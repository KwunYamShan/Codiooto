# Codiooto
[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
微博,微信图库效果,微信视频拖放效果,适配状态栏 、屏幕旋转 、全屏 、长图、GIF、视频
## 高仿微信图片拖拽退出
实现方式：点击相册中的条目时，记录了该条目的坐标位置和大小，然后打开了一个透明的activity，activity相册的信息后做了个属性动画（共享元素也可以实现此功能）
1. 将ui和diooto类库完全分离，支持完全自定义UI
2. 自动更新图片大小
3. 适配状态栏
4. 适配屏幕旋转
5. 适配全屏
6. 适配长图
7. 适配GIF
8. 适配视频
## 使用
#### 添加依赖 
1. App下的build.gradle添加依赖类库  [ ![Download](https://api.bintray.com/packages/kwunyamshan/maven/Codiooto/images/download.svg) ](https://bintray.com/kwunyamshan/maven/Codiooto/_latestVersion)
```Java
    //版本号:x.x.x替换Download中的版本号
    implementation 'com.wh.diooto:Codiooto:x.x.x'
```
2. Project下的build.gradle添加仓库地址
```Java
buildscript {
    repositories {
        maven { url"https://dl.bintray.com/kwunyamshan/maven" }    
        ...
    }
    dependencies {
        //gradle版本与相应插件版本的对应关系 3.4.0+  —>  5.1.1+
        classpath 'com.android.tools.build:gradle:3.4.1'
        ...
    }
}

allprojects {
    repositories {
        maven { url"https://dl.bintray.com/kwunyamshan/maven" }
        ...
    }
}
```
#### 图片模式
```Java
                    Diooto diooto = new Diooto(context)
                            .urls(activityPosition == 2 ? longImageUrl : normalImageUlr)
                            //图片或者视频
                            .type(DiootoConfig.PHOTO)
                            .immersive(isImmersive)
                            //是否需要加退出动画
                            .isAnim(true)
                            //点击的位置 如果你的RecyclerView有头部View  则使用 .position(holder.getAdapterPosition(),headSize) headSize为头部布局数量
                            .position(holder.getAdapterPosition(), 1)
                            //可以传recylcerview自动识别(需要传在item布局中的viewId)  也可以手动传view数组
                            .views(mRecyclerView, R.id.srcImageView)
                            //在显示原图之前显示的图片  如果你列表使用Glide加载  这里也使用Glide加载
                            .loadPhotoBeforeShowBigImage((sketchImageView, position12) -> {
                                sketchImageView.displayImage(normalImageUlr[position]);
                                sketchImageView.setOnLongClickListener(v -> {
                                    Toast.makeText(DisplayActivity.this, "Long click", Toast.LENGTH_SHORT).show();
                                    return false;
                                });
                            })
                            .start(ImageActivity.class);
```
#### 视频模式
```Java
 //加载视频
                    Diooto diooto = new Diooto(context)
                            .urls(normalImageUlr[position])
                            .position(holder.getAdapterPosition())
                            .views(holder.srcImageView)
                            .type(DiootoConfig.VIDEO)
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
                                videoView.setUp("https://vdse.bdstatic.com//28df11aa5252020ace6fa4321f5a50e3.mp4?authorization=bce-auth-v1/fb297a5cc0fb434c971b8fa103e8dd7b/2017-05-11T09:02:31Z/-1//b3d16a3d534465108ca76bf89d90f86e5b1be6543119a9d864b6d3c315251725");
                                videoView.start();
                                dragDiootoView.notifySize(1920, 1080);
                                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
                            })
                            //到最小状态的接口
                            .onFinish(dragDiootoView -> MediaPlayerManager.instance().releasePlayerAndView(context))
                            .start(ImageActivity.class);
```
### 在此感谢
  [特别感谢Diooto，支持原创。在原创的基础上将UI与类库进行了分离，并增加了一些功能](https://github.com/moyokoo/Diooto)
  
  原作者QQ群:[470160953](http://qm.qq.com/cgi-bin/qm/qr?k=PfTIlZp3p1_VBZwOMq_iFRBE6Xn89uz8)

### 效果图
  <img src="https://github.com/moyokoo/Media/blob/master/diooto1.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto2.gif?raw=true" height="500"/><img src="https://github.com/moyokoo/Media/blob/master/diooto3.gif?raw=true" height="500"/>