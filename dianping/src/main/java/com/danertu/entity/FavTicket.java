package com.danertu.entity;

public class FavTicket {
	private String name;
	private String guid;
	private String money;
	private String withOthers;

	public FavTicket() {
	}

	public FavTicket(String name, String guid, String money, String withOthers) {
		super();
		this.name = name;
		this.guid = guid;
		this.money = money;
		this.withOthers = withOthers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getWithOthers() {
		return withOthers;
	}
	public void setWithOthers(String withOthers) {
		this.withOthers = withOthers;
	}
}
