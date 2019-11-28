package net.moyokoo.diooto.config;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DiootoConfig implements Parcelable {

    public static int PHOTO = 1;
    public static int VIDEO = 2;
    private int type = PHOTO;
    private String[] imageUrls;
    private String[] originalUrls;
    private boolean isFullScreen = false;
    private List<ContentViewOriginModel> contentViewOriginModels;
    private int position;
    private boolean immersive;
    private int headerSize;
    private int footerSize;
    private boolean isAnim;

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

  public int getFooterSize() {
    return footerSize;
  }

  public void setFooterSize(int footerSize) {
    this.footerSize = footerSize;
  }


  public boolean isImmersive() {
        return immersive;
    }

    public void setImmersive(boolean immersive) {
        this.immersive = immersive;
    }

    public void setAnim(boolean anim){
        this.isAnim  = anim;
    }

    public boolean isAmin(){
        return isAnim;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String[] getOriginalUrls() {
        return originalUrls;
    }

    public void setOriginalUrls(String[] originalUrls) {
        this.originalUrls = originalUrls;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public List<ContentViewOriginModel> getContentViewOriginModels() {
        return contentViewOriginModels;
    }

    public void setContentViewOriginModels(List<ContentViewOriginModel> contentViewOriginModels) {
        this.contentViewOriginModels = contentViewOriginModels;
    }

    public DiootoConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeStringArray(this.imageUrls);
        dest.writeStringArray(this.originalUrls);
        dest.writeByte(this.isFullScreen ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.contentViewOriginModels);
        dest.writeInt(this.position);
        dest.writeByte(this.immersive ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnim ? (byte) 1 : (byte) 0);
        dest.writeInt(this.headerSize);
        dest.writeInt(this.footerSize);
    }

    protected DiootoConfig(Parcel in) {
        this.type = in.readInt();
        this.imageUrls = in.createStringArray();
        this.originalUrls = in.createStringArray();
        this.isFullScreen = in.readByte() != 0;
        this.contentViewOriginModels = in.createTypedArrayList(ContentViewOriginModel.CREATOR);
        this.position = in.readInt();
        this.immersive = in.readByte() != 0;
        this.isAnim = in.readByte() != 0;
        this.headerSize = in.readInt();
        this.footerSize = in.readInt();
    }

    public static final Creator<DiootoConfig> CREATOR = new Creator<DiootoConfig>() {
        @Override
        public DiootoConfig createFromParcel(Parcel source) {
            return new DiootoConfig(source);
        }

        @Override
        public DiootoConfig[] newArray(int size) {
            return new DiootoConfig[size];
        }
    };
}
