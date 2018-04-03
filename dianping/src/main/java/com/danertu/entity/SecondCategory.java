package com.danertu.entity;

public class SecondCategory {
	private String SecondID;
	private String CategoryName;

	public SecondCategory() {
		SecondID = "0";
		CategoryName = "";
	}

	public SecondCategory(String _SecondID, String _CategoryName) {
		SecondID = _SecondID;
		CategoryName = _CategoryName;
	}

	@Override
	public String toString() {
		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
		return CategoryName;
	}

	public String GetSecondID() {
		return SecondID;
	}

	public String GetCategoryName() {
		return CategoryName;
	}
}
