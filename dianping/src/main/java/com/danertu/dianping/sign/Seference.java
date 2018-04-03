package com.danertu.dianping.sign;

import android.content.Context;
import android.content.SharedPreferences;

public class Seference {

	public Context mcontext;
	public String signFileName="sign";
	
	public Seference(Context context) {
		this.mcontext = context;
	}
	
	public void savePreferenceData(String filename, String key, String value) {
		SharedPreferences.Editor sharedata = this.mcontext
				.getSharedPreferences(filename, 0).edit();
		sharedata.putString(key, value);
		sharedata.apply();
	}
	
	public String getPreferenceData(String filename, String key,String defaultValue) {
		String temp = "";
		SharedPreferences sharedata = this.mcontext.getSharedPreferences(
				filename, 0);
		temp = sharedata.getString(key, defaultValue);
		return temp;
	}
	
	public void clearPreData(String filename){
		SharedPreferences.Editor sharedata = this.mcontext.getSharedPreferences(filename, 0).edit();
		sharedata.clear();
		sharedata.apply();
	}
}
