package com.danertu.dianping;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.danertu.entity.Messagebean;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;

public class MessageDetail extends BaseActivity {

    private String url;
    String id = null;
    String title, time;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstLoad = true;
        setContentView(R.layout.activity_msg_detail);
        Bundle bundle = getIntent().getExtras();
        initIntentMsg(bundle);
        initTitle();
        initWebViewWidget();
        if (TextUtils.isEmpty(url)) {
            finish();
        }
        this.startWebView(url);
    }

    private void startWebView(String url) {
        if (webView != null)
            webView.loadUrl(url);
    }

    private void initWebViewWidget() {
        webView = (WebView) findViewById(R.id.mWebView1);
        initWebSettings();
    }

    private void initTitle() {
        Button b_back = (Button) findViewById(R.id.b_title_back4);
        TextView tv_title = (TextView) findViewById(R.id.tv_title4);
        tv_title.setText("资讯详细");
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        ImageButton ib_opera = (ImageButton) findViewById(R.id.b_title_operation4);
        ib_opera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String type = "android";
                String shopid = "";
                String imgPath = "";
                String targetPath = String.format("http://www.danertu.com/mobile/announcement/AnnouncementDetail.htm?guid=%s&platform=android", id);
                String description = title;
                String title = "单耳兔资讯";
                share(type, shopid, title, imgPath, targetPath, description);
            }
        });
    }

    private void initIntentMsg(Bundle bundle) {
        url = bundle.getString("url");
        id = bundle.getString(Messagebean.COL_ID);
        title = bundle.getString(Messagebean.COL_MESSAGETITLE);
        time = bundle.getString(Messagebean.COL_MODIFLYTIME);
        Logger.e("web","url="+url+",title="+title+",time="+time);
    }

    protected void initWebSettings() {
        webView.addJavascriptInterface(new DemoJs(), "app");
        this.webView.setWebViewClient(new MWebViewClient(this, "app") {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadDialog();
                if (isFirstLoad && CommonTools.isConnected(getContext())) {
                    new GetNotice().execute();
                    isFirstLoad = false;
                }
            }

        });
    }

    private class GetNotice extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String msgJson = AppManager.getInstance().postGetNotice(id);
            return msgJson.replaceAll("\n|\r", "");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            webView.loadUrl("javascript:javaLoadAnnouncementDetail('" + title + "','" + time + "','" + result + "')");
        }
    }

    private class DemoJs {
        public void testCallBack(String testParam) {
            Logger.w("DemoJs", testParam);
        }
    }

    @Override
    protected void findViewById() {
    }

    @Override
    protected void initView() {
    }

}