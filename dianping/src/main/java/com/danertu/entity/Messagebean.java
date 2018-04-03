package com.danertu.entity;

public class Messagebean extends BaseModel {

	// model columns
	public final static String COL_ID = "id";
	public final static String COL_MESSAGETITLE = "messageTitle";
	public final static String COL_MODIFLYTIME = "ModiflyTime";

	private String id;
	private String messageTitle;
	private String modiflyTime;

	public String getModiflyTime() {
		return modiflyTime;
	}

	public void setModiflyTime(String modiflyTime) {
		this.modiflyTime = modiflyTime;
	}

	public Messagebean() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	@Override
	public String toString() {
		return "Messagebean{" +
				"id='" + id + '\'' +
				", messageTitle='" + messageTitle + '\'' +
				", modiflyTime='" + modiflyTime + '\'' +
				'}';
	}
}
