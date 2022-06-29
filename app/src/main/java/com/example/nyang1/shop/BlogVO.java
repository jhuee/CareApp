package com.example.nyang1.shop;

public class BlogVO {
    private String title;
    private String link;
    private String description;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }



    @Override
    public String toString() {
        return "BlogVO{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}