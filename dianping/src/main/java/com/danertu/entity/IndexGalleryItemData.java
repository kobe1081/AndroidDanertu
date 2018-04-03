package com.danertu.entity;

import android.graphics.Bitmap;

public class IndexGalleryItemData {
	private String id = null;
	private String imageUrl = null;
	private String price = null;
	private Bitmap bmap = null;
	private String name = null;
	private String detail = null;
	public Bitmap getBmap() {
		return bmap;
	}

	public void setBmap(Bitmap bmap) {
		this.bmap = bmap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	 
}
