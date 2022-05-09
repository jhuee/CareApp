package com.example.nyang1.shop;

import android.graphics.Bitmap;

public class ListViewItem {

    private Bitmap iconDrawable;
    private String contentStr;
    private String titleStr;
    private String rating;
    private String reviewCount;


    public void setTitle(String title) {
        titleStr = title;
    }

    public void setIcon(Bitmap icon) {
        iconDrawable = icon;
    }

    public void setContent(String content) {
        contentStr = content;
    }

    public void setRating(String rating){ this.rating = rating;}

    public void setReviewCount(String reviewCount){ this.reviewCount = reviewCount;}

    public Bitmap getIcon() {
        return this.iconDrawable;
    }

    public String getContent() {
        return this.contentStr;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getRating(){ return this.rating; }

    public String getReviewCount(){ return this.reviewCount; }
}