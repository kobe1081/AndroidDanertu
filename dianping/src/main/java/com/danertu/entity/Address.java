package com.danertu.entity;

public class Address {

	private String name;
	private String tel;
	private String mobile;
	private String address;
	private String isDefault;
	private String modifyTime;
	private String guid;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return "Address{" +
				"name='" + name + '\'' +
				", tel='" + tel + '\'' +
				", mobile='" + mobile + '\'' +
				", address='" + address + '\'' +
				", isDefault='" + isDefault + '\'' +
				", modifyTime='" + modifyTime + '\'' +
				", guid='" + guid + '\'' +
				'}';
	}
}
