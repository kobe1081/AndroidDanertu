package com.danertu.entity;


public class ShopCar {
	

	private String imageUrl;
	private String proName;
	private String price;
	private String count;
	public ShopCar(String imageUrl,String proName,String price,String count) {
		super();
		this.imageUrl = imageUrl;
		this.proName=proName;
		this.price=price;
		this.setCount(count);
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
}
