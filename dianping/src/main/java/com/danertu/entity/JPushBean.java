package com.danertu.entity;

/**
 * Created by Viz on 2017/9/25.
 */

public class JPushBean {
    int _id;
    String title;
    String message;
    String pushTime;

    public JPushBean(int _id, String title, String message, String pushTime) {
        this._id=_id;
        this.title = title;
        this.message = message;
        this.pushTime = pushTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    @Override
    public String toString() {
        return "JPushBean{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", pushTime='" + pushTime + '\'' +
                '}';
    }
}
