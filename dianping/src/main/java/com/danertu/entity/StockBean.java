package com.danertu.entity;

/**
 * Created by Viz on 2017/11/10.
 */

public class StockBean {
    private int img;
    private String name;
    private double price;
    private int count;

    public StockBean(int img, String name, double price, int count) {
        this.img = img;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
