package com.danertu.entity;

/**
 * 头条新闻实体类
 * Created by Viz on 2017/9/22.
 */

public class NewsBean {
    private String newsTitle;
    private String newsTime;

    public NewsBean() {
    }

    public NewsBean(String newsTitle, String newsTime) {
        this.newsTitle = newsTitle;
        this.newsTime = newsTime;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }
}
