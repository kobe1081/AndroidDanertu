package com.danertu.entity;

public class Category {
    private String ID;
    private String CategoryName;

    public Category() {
        ID = "0";
        CategoryName = "";
    }

    public Category(String _ID, String _CategoryName) {
        ID = _ID;
        CategoryName = _CategoryName;
    }

    @Override
    public String toString() {
        // 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
        return CategoryName;
    }

    public String GetID() {
        return ID;
    }

    public String GetValue() {
        return CategoryName;
    }

}
