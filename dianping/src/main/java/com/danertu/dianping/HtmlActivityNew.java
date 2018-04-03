package com.danertu.dianping;

import cn.xiaoneng.coreapi.ChatParamsBody;
import wl.codelibrary.widget.IOSDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.tools.XNUtil;
import com.danertu.widget.CommonTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class HtmlActivityNew extends BaseWebActivity {
    private View title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationUtil(this).startLocate();
        title = findViewById(R.id.title2);
        title.setVisibility(View.GONE);
        webView.addJavascriptInterface(this, "app");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        Intent i = getIntent();
        String url = "";
        if (i.getBooleanExtra("push", false)) {
            //如果是推送下来的活动链接，需要需要判断当前登录的用户是否已开店来拼接链接
            new getAgentId().execute(i.getStringExtra("url"));
        } else {
            url = i.getStringExtra("url");
            boolean isAutoShowDialog = Boolean.parseBoolean(i.getStringExtra("showDialog"));
            setAutoHideDialog(!isAutoShowDialog);
            startWebView(url);
        }

    }

    private IOSDialog dialog;

    /**
     * 获取店铺id
     */
    private class getAgentId extends AsyncTask<String, Integer, String> {

        //async task
        protected String doInBackground(String... arg0) {
            String url = arg0[0];
            String agentId = "";
            try {
                //是否已开店
                String param = "apiid|0141,;shopid|" + getUid();
                //{"val":[{"ID":"10270","ShopName":"测试42"}]}
                //{"val":[]}
                String isOpenShop = getData(param);
                //未开店,触发exception
                Object val = new JSONObject(isOpenShop).getJSONArray("val").get(0);
                //已开店,
                agentId = getUid();
//                param = "apiid|0245,;shopid|" + getUid();
//                String result = getData(param);
//
//                //[{"ShopName":"ck醇康","memberid":"chunkang","Mobile":"4009952220","RealName":"醇康贸易"}]
//                JSONObject object = new JSONObject(result).getJSONObject("LeaderInfo").getJSONArray("LeaderBean").getJSONObject(0);
//                String memberid = object.getString("memberid");
//                String shopName = object.getString("ShopName");
//                if (TextUtils.isEmpty(shopName)) {
//                    if ("chunkang".equals(memberid)) {
//                        agentId = "15017339307";
//                    } else {
//                        agentId = memberid;
//                    }
//                } else {
//                    agentId = getUid();
//                }
                return url + "?agentid=" + agentId + "&platform=android";
            } catch (Exception e) {
                agentId = Constants.CK_SHOPID;
                e.printStackTrace();
            }
            return url + "?agentid=" + agentId + "&platform=android";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                startWebView(result);
            } else {
                jsShowMsg("数据加载失败");
                finish();
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @JavascriptInterface
    public void quitAccount() {
        if (dialog == null) {
            dialog = new IOSDialog(this);
            dialog.setTitle("退出登录");
            dialog.setMessage("退出后不会删除任何历史数据，下次\n登录依然可以使用本帐号");
            dialog.setPositiveButton("是", new View.OnClickListener() {
                public void onClick(View v) {
                    jsShowMsg("注销登录");
                    sendLogoutBroadcast();
                    String uid = getUid();

                    db.DeleteLoginInfo(getContext(), uid);

                    //防止登录删除不成功
                    if (db.GetLoginUid(getContext()).equals(uid)) {
                        if (Constants.isDebug) {
                            CommonTools.showShortToast(getContext(), "登录后登录信息更新不成功");
                        }
                        db.DeleteLoginInfo(getContext(), uid);
                    }
                    Logger.e("test", "HtmlActivityNew quitAccount getUid=" + uid);
                    dialog.dismiss();
                    application.backToActivity("IndexActivity");
                }
            });
            dialog.setNegativeButton("否", new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    /**
     * 发出退出登录广播通知首页刷新
     */
    private void sendLogoutBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(Constants.LOGOUT_SUCCESS_BROADCAST);
        manager.sendBroadcast(intent);
    }

    @JavascriptInterface
    public void setShopID(String shopid) {
        SplashActivity.EXTENSION_SHOPID = shopid;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 2017年11月8日
     * 添加在线客服入口
     */
    @JavascriptInterface
    public void jsContact(String proId, String proName, String price, String imgUrl, String goodsUrl, String showUrl) {
        XNUtil xnUtil = new XNUtil(this);

        xnUtil.setTitle(proName);
        xnUtil.setUsername(getUid());
        xnUtil.setUserid(getUid());
        xnUtil.setOrderprice(price);
        xnUtil.postCustomerTrack();

        ChatParamsBody itemparam = xnUtil.genProParam(proId, proName, Double.parseDouble(price), imgUrl, goodsUrl, showUrl);
        xnUtil.setItemparam(itemparam);
        xnUtil.communicte();

    }
}
