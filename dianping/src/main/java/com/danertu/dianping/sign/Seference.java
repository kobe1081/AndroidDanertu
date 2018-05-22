package com.danertu.dianping.sign;

import android.content.Context;
import android.content.SharedPreferences;

import com.danertu.tools.Logger;

import static android.content.Context.MODE_PRIVATE;

public class Seference {

    public Context mcontext;
    public String signFileName = "sign";

    public Seference(Context context) {
        this.mcontext = context;
    }

    public void savePreferenceData(String filename, String key, String value) {
        SharedPreferences.Editor sharedata = this.mcontext.getSharedPreferences(filename, MODE_PRIVATE).edit();
        sharedata.putString(key, value);
        sharedata.apply();
    }

    public String getPreferenceData(String filename, String key, String defaultValue) {
        String temp = "";
        SharedPreferences sharedata = this.mcontext.getSharedPreferences(filename, MODE_PRIVATE);
        temp = sharedata.getString(key, defaultValue);
        return temp;
    }

    public void clearPreData(String filename) {
        SharedPreferences.Editor sharedata = this.mcontext.getSharedPreferences(filename, MODE_PRIVATE).edit();
        sharedata.clear();
        sharedata.apply();
    }
}
