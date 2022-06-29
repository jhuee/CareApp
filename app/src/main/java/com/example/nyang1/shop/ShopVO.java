package com.example.nyang1.shop;


public class ShopVO extends BlogVO{
    private int lprice;
    private String image, mallName;


    public int getLPrice() {
        return lprice;
    }

    public void setLPrice(int lprice) {
        this.lprice = lprice;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {this.mallName = mallName;}

    @Override
    public String toString() {
        return "ShopVO{" +
                " lprice=" + lprice +
                ", image='" + image + '\'' +
                " mallName=" + mallName +
                '}';
    }
}