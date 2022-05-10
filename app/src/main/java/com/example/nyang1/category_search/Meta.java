package com.example.nyang1.category_search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Meta implements Parcelable {

    @SerializedName("same_name")
    @Expose
    private SameName sameName;
    @SerializedName("pageable_count")
    @Expose
    private Integer pageableCount;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("is_end")
    @Expose
    private Boolean isEnd;

    protected Meta(Parcel in) {
        sameName = in.readParcelable(SameName.class.getClassLoader());
        if (in.readByte() == 0) {
            pageableCount = null;
        } else {
            pageableCount = in.readInt();
        }
        if (in.readByte() == 0) {
            totalCount = null;
        } else {
            totalCount = in.readInt();
        }
        byte tmpIsEnd = in.readByte();
        isEnd = tmpIsEnd == 0 ? null : tmpIsEnd == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sameName, flags);
        if (pageableCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageableCount);
        }
        if (totalCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalCount);
        }
        dest.writeByte((byte) (isEnd == null ? 0 : isEnd ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Meta> CREATOR = new Creator<Meta>() {
        @Override
        public Meta createFromParcel(Parcel in) {
            return new Meta(in);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };

    public SameName getSameName() {
        return sameName;
    }

    public void setSameName(SameName sameName) {
        this.sameName = sameName;
    }

    public Integer getPageableCount() {
        return pageableCount;
    }
}