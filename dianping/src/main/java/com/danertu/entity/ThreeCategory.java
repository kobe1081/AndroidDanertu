package com.danertu.entity;

public class ThreeCategory {
	private String threeID;
	private String categoryName;
	private String keyWords = null;
	private String family = null;
	private String phoneImage = null;
	private String backgroundImage = null;
	public String getThreeID() {
		return threeID;
	}
	public void setThreeID(String threeID) {
		this.threeID = threeID;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getPhoneImage() {
		return phoneImage;
	}
	public void setPhoneImage(String phoneImage) {
		this.phoneImage = phoneImage;
	}
	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

//	public ThreeCategory() {
//		ThreeID = "0";
//		CategoryName = "";
//	}
//
//	public ThreeCategory(String _ThreeID, String _CategoryName) {
//		ThreeID = _ThreeID;
//		CategoryName = _CategoryName;
//	}
//
//	@Override
//	public String toString() {
//		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
//		// TODO Auto-generated method stub
//		return CategoryName;
//	}
//
//	public String GetThreeID() {
//		return ThreeID;
//	}
//
//	public String GetCategoryName() {
//		return CategoryName;
//	}
}
