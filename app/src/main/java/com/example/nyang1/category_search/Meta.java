package com.example.nyang1.category_search;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Meta implements Parcelable {

    @SerializedName("same_name")
    @Expose
    private SameName sameName;
    @SerializedName("pageable_count")
    @Expose
    private int pageableCount;
    @SerializedName("total_count")
    @Expose
    private int totalCount;
    @SerializedName("is_end")
    @Expose
    private boolean isEnd;

    public class SameName {
        private List<String> region;
        private String keyword;
        private String selected_region;
    }

    public class Documents {
        private String place_name;
        private String distance;
        private String place_url;
        private String category_name;
        private String address_name;
        private String road_address_name;
        private String id;
        private String phone;
        private String category_group_code;
        private String category_group_name;
        private String x;
        private String y;
    }

    public class KeyWord {
        private Meta meta;
        private List<Documents> documents;
    }


    public SameName getSameName() {
        return sameName;
    }

    public void setSameName(SameName sameName) {
        this.sameName = sameName;
    }

    public Integer getPageableCount() {
        return pageableCount;
    }

    public void setPageableCount(Integer pageableCount) {
        this.pageableCount = pageableCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(Boolean isEnd) {
        this.isEnd = isEnd;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable((Parcelable) this.sameName, flags);
        dest.writeValue(this.pageableCount);
        dest.writeValue(this.totalCount);
        dest.writeValue(this.isEnd);
    }

    public Meta() {
    }

    protected Meta(Parcel in) {
        this.sameName = in.readParcelable(SameName.class.getClassLoader());
        this.pageableCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isEnd = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Meta> CREATOR = new Creator<Meta>() {
        @Override
        public Meta createFromParcel(Parcel source) {
            return new Meta(source);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };
}