package com.danertu.widget;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.config.Constants;
import com.danertu.tools.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MWebViewClient extends WebViewClient {
    private boolean isError = false;
    private String iface = null;
    public static final String ERROR_PAGE = "file:///android_asset/Android_errorPage.html";
    Context act;
    final String TAG = "MWebViewClient";

    public MWebViewClient(Context act, String ifaceName) {
        this.act = act;
        this.iface = ifaceName;

    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse response = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            response = super.shouldInterceptRequest(view, url);
            String name = "jquery-2.1.1.js";
            String name1 = "jquery-2.1.1.min.js";
            String name2 = "jquery.min.js";
            String name3 = "jquery.js";
            String name4 = "com/com.js";
            try {
                if (url.contains(name)) {
                    response = getWebResource(name);
                } else if (url.contains(name1)) {
                    response = getWebResource(name1);
                } else if (url.contains(name2)) {
                    response = getWebResource(name2);
                } else if (url.contains(name3)) {
                    response = getWebResource(name3);
                } else if (url.contains(name4)) {
                    response = getWebResource(name4);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    //    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        //    return super.shouldOverrideUrlLoading(view, url);
//        //重写这个用于页面点击号码开启系统拨号盘
//        if (url.startsWith("tel:")) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            act.startActivity(intent);
//            return true;
//        }
//        view.loadUrl(url);
//        return true;
//    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private WebResourceResponse getWebResource(String name) throws IOException {
        return new WebResourceResponse("application/x-javascript", "UTF-8", act.getAssets().open("template/js/" + name));
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Logger.v(TAG, "onReceivedError( int " + errorCode + ", String " + description + ", String " + failingUrl + ")");
        super.onReceivedError(view, errorCode, description, failingUrl);
        view.loadUrl(ERROR_PAGE);
        isError = true;
    }

    /**
     * https发生错误回调
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Logger.v(TAG, "onReceivedSslError( handler " + handler.toString() + ", SslError " + error.toString() + ")");
//        super.onReceivedSslError(view, handler,error);
//        view.loadUrl(ERROR_PAGE);
//        isError = true;
        handler.proceed();//继续加载发生错误的网址
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Logger.v(TAG, "onPageFinished( String " + url + ")");
        super.onPageFinished(view, url);
        //移除联通注入的广告
        view.loadUrl(Constants.IFACE + "try{document.getElementById('tlbstoolbar').style.display = 'none';document.getElementById('tlbstoolbar').parentNode.removeChild(this);}catch(e){}");
        if (isError) {
            view.loadUrl(Constants.IFACE + "javaLoadErrInfo('" + iface + "')");
            isError = false;
        }
        httpState = -1;
    }

    private String description = "";
    private WebView view;
    private String url;
    private Bitmap favicon;

    int errorFlag = 0;

    @Override
    public void onPageStarted(WebView webView, String u, Bitmap bitmap) {
        Logger.v(TAG, "onPageStarted( String " + u + ", Bitmap " + bitmap + ")");
        this.view = webView;
        this.url = u;
        this.favicon = bitmap;
        String flag = url.substring(0, 4);
        if (errorFlag > 2) {
            if (act instanceof Activity) {
                ((Activity) act).finish();
            }
        } else if (!flag.equals("http")) {
            if (url.equals(ERROR_PAGE)) {
                errorFlag++;
            }
            super.onPageStarted(view, url, favicon);
        } else if (httpState != -1) {
            super.onPageStarted(view, url, favicon);
        } else {
            new Thread() {
                public void run() {
                    final int code = getHttpState(url);
                    handler.sendEmptyMessage(code);
                }
            }.start();
        }
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == 200) {
                onPageStarted(view, url, favicon);
            } else {
                if (TextUtils.isEmpty(description)) {
                    description = "code is defind by myself, error url";
                }
                onReceivedError(view, code, description, url);
            }
        }
    };

    private int httpState = -1;

    public int getHttpState(String url) {
        long st, et;
        st = System.currentTimeMillis();

//        HttpUriRequest params = null;
//        HttpClient client = null;
//        url="https://danertu.com";
//        url="https://appweb.danertu.com:444/Android/AndroidIndex.html";
//        try {
//            params = new HttpHead(url);// 服务器响应只返回消息头，一般用于检测链接的有效性
//            client = new DefaultHttpClient();
//            HttpResponse response = client.execute(params);
//            httpState = response.getStatusLine().getStatusCode();
//
//            Header[] heads = response.getAllHeaders();
//            int count = 0;
//            Logger.v("消息头" + count++, "stateCode: "+httpState);
//            for (Header item : heads) {
//                Logger.v("消息头" + count++, item.toString());
//            }
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            description = e.toString();
//            httpState = 0;
//        } finally{
//            if(params != null && !params.isAborted()){
//                params.abort();
//            }
//            if(client != null){
//                client = null;
//            }
//        }

        OkHttpClient okHttpClient = null;
        Request request = null;

        try {
            okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .build();
            request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            httpState = response.code();
            Headers headers = response.headers();
            int count = 0;
            Logger.v("消息头" + count++, "stateCode: " + httpState);
            for (int i = 0; i < headers.size(); i++) {
                Logger.v("消息头" + count++, headers.get(headers.name(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            description = e.toString();
            httpState = 0;
        } finally {
            if (okHttpClient != null)
                okHttpClient = null;
        }
        et = System.currentTimeMillis();
        Logger.v("耗时", (et - st) + "毫秒");
        return httpState;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

}
