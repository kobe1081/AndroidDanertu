package com.danertu.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.config.ApiService;
import com.config.Constants;
import com.config.SSLSocketClient;
import com.danertu.db.DBManager;
import com.danertu.entity.CouponCountBean;
import com.danertu.entity.LeaderBean;
import com.danertu.entity.ShopStateBean;
import com.danertu.tools.AppManager;
import com.danertu.tools.Base64;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huang on 2017/11/11.
 */

public abstract class BaseModel {
    public String TAG = getClass().getSimpleName();
    public static final int MSG_SERVER_ERROR = 1;
    public static final int RESULT_OK = 200;
    public Retrofit retrofit;
    private DeviceTag dTag = null;
    protected DBManager db = null;
    protected Context context;
//    protected AppManager appManager;

    public BaseModel(Context context) {
        this.context = context;
//        appManager = AppManager.getInstance();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Interceptor myInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Logger.i(TAG, request.toString());
                //在这里获取到request后就可以做任何事情了
                String paramsStr = "";
                Headers headers = request.headers();
                if (headers != null && Constants.isDebug) {
                    Set<String> names = headers.names();
                    StringBuilder builder1 = new StringBuilder();
                    for (String s : names) {
                        builder1.append(s).append(":").append(headers.get(s));
                    }
                    Logger.i(TAG, "\nheaders:\n" + builder1.toString() + "\n");
                }
                if (request.method().equals("GET")) {
                    request = addGetParams(request);
                } else if (request.method().equals("POST")) {
                    request = addPostParams(request);
                }
                RequestBody requestBody = request.body();
                if (requestBody != null && Constants.isDebug) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = Charset.forName("UTF-8");
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName("UTF-8"));
                    }
                    paramsStr = buffer.readString(charset);
                    Logger.i(TAG, "\n" + request.url() + "\n" + paramsStr);
                }
                return chain.proceed(request);
            }

        };
        if (Constants.isDebug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //打印retrofit日志
                    Logger.i(TAG, "retrofitBack = " + message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        SSLSocketFactory sslSocketFactory = SSLSocketClient.getSSLSocketFactory();

//        TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
        builder.addInterceptor(myInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间为30s
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .sslSocketFactory(sslSocketFactory, new TrustAllCerts());//配置

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_API_URL)
//                .baseUrl("https://website.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
    }

    private class TrustAllCerts implements X509TrustManager {
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

    /**
     * 给get方法添加授权参数
     *
     * @param request
     * @return
     */
    private Request addGetParams(Request request) throws UnsupportedEncodingException {
        HttpUrl httpUrl = request.url();
        Set<String> nameSet = httpUrl.queryParameterNames();
        nameSet.add("dateline");
        nameSet.add("tid");
        nameSet.add("appver");
        nameSet.add("platform");
        httpUrl = httpUrl.newBuilder()
                .addQueryParameter("dateline", String.valueOf(System.currentTimeMillis()))
                .addQueryParameter("appver", String.valueOf(getVersionCode(context)))
                .addQueryParameter("platform", Constants.APP_PLATFORM)
                .addQueryParameter("tid", getUid(context))
                .build();


        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(nameSet);
        Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nameList.size(); i++) {
            String name = nameList.get(i);
            List<String> parameterValues = httpUrl.queryParameterValues(name);
            stringBuilder.append(name).append("=").append(parameterValues != null && parameterValues.size() > 0 ? parameterValues.get(0) : "").append("&");
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }
        String encode = Base64.encode(stringBuilder.toString().getBytes("utf-8"));
//        Logger.e(TAG, "sign=" + encode);
        httpUrl = httpUrl.newBuilder()
                .addQueryParameter("signs", encode)
                .addQueryParameter("token", Constants.USER_TOKEN)
                .build();

        request = request.newBuilder().url(httpUrl).build();
        return request;
    }

    /**
     * 给post请求添加授权参数
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private Request addPostParams(Request request) throws UnsupportedEncodingException {
        if (request.body() instanceof FormBody) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            FormBody formBody = (FormBody) request.body();
            //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的
            //使用原始值加密，不转码
//            for (int i = 0; i < formBody.size(); i++) {
////                bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
//                bodyBuilder.add(formBody.name(i), formBody.value(i));
//            }
//
//            formBody = bodyBuilder.add("dateline", String.valueOf(System.currentTimeMillis()))
//                    .add("tid", getUid(context))
//                    .add("appver", String.valueOf(getVersionCode(context)))
//                    .add("platform", Constants.APP_PLATFORM)
//                    .build();

            LinkedHashMap<String, String> bodyMap = new LinkedHashMap<>();
            List<String> nameList = new ArrayList<>();
            for (int i = 0; i < formBody.size(); i++) {
                String name = formBody.name(i);
                String encodedValue = formBody.value(i);
                nameList.add(name);
                bodyMap.put(name, encodedValue);
            }
            nameList.add("dateline");
            nameList.add("tid");
            nameList.add("appver");
            nameList.add("platform");
            bodyMap.put("dateline", String.valueOf(System.currentTimeMillis()));
            bodyMap.put("tid", getUid(context));
            bodyMap.put("appver", String.valueOf(getVersionCode(context)));
            bodyMap.put("platform", Constants.APP_PLATFORM);
            Collections.sort(nameList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            bodyMap = sortMap(bodyMap);
            for (String s : bodyMap.keySet()) {
                bodyBuilder.add(s, bodyMap.get(s));
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < nameList.size(); i++) {
                String name = nameList.get(i);
                builder.append(name).append("=").append(bodyMap.get(name)).append("&");
            }
            builder.delete(builder.length() - 1, builder.length());
            String encode = Base64.encode(builder.toString().getBytes("utf-8"));
//            Logger.e(TAG, "\nparam=" + builder.toString());
//            Logger.e(TAG, "\nsign=" + encode);
            bodyBuilder.addEncoded("signs", encode)
                    .addEncoded("token", Constants.USER_TOKEN);
            formBody = bodyBuilder.build();
            request = request.newBuilder().post(formBody).build();
        }
        return request;
    }

    public String getVersionName(Context context) {
        return CommonTools.getVersionName(context);
    }

    public int getVersionCode(Context context) {
        return CommonTools.getVersionCode(context);
    }

    /**
     * 参数列表排序
     *
     * @param map
     * @return
     */
    public LinkedHashMap<String, String> sortMap(Map<String, String> map) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        //先转成ArrayList集合
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<String, String> entry : list) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return hashMap;
    }


    /**
     * 检查用户是否已开店
     *
     * @param loginId
     * @param callBack
     */
    public void checkIsOpenShop(String loginId, final ModelParamCallBack callBack) {

//        String json = appManager.checkShopState(loginId);
//        if (TextUtils.isEmpty(json)) {
//            callBack.requestError(null);
//            return;
//        }
//        ShopStateBean shopStateBean = JSONObject.parseObject(json, ShopStateBean.class);
//        if (shopStateBean == null) {
//            callBack.requestError(null);
//            return;
//        }
//        if ("false".equals(shopStateBean.getResult()) && "-1".equals(shopStateBean.getCode())) {
//            callBack.tokenException(shopStateBean.getCode(), shopStateBean.getInfo());
//            return;
//        }
//        if (shopStateBean.getVal() == null || shopStateBean.getVal().size() == 0) {
//            callBack.requestError(null);
//            return;
//        }
//        callBack.requestSuccess(shopStateBean);

        Call<ShopStateBean> call = retrofit.create(ApiService.class).checkShopState("0141", loginId);
        call.enqueue(new Callback<ShopStateBean>() {
            @Override
            public void onResponse(Call<ShopStateBean> call, Response<ShopStateBean> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(response.body());

            }

            @Override
            public void onFailure(Call<ShopStateBean> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }

    /**
     * 获取用户上级的信息
     *
     * @param loginId
     * @param callBack
     */
    public void getLeaderInfo(String loginId, final ModelParamCallBack callBack) {

//        String json = appManager.getLeaderInfo(loginId);
//        if (TextUtils.isEmpty(json)) {
//            callBack.requestError(null);
//            return;
//        }
//        LeaderBean leaderBean = JSONObject.parseObject(json, LeaderBean.class);
//        if (leaderBean == null) {
//            callBack.requestError(null);
//            return;
//        }
//        if ("false".equals(leaderBean.getResult()) && "-1".equals(leaderBean.getCode())) {
//            callBack.tokenException(leaderBean.getCode(), leaderBean.getInfo());
//            return;
//        }
//        if (leaderBean.getLeaderInfo() == null || leaderBean.getLeaderInfo().getLeaderBean() == null || leaderBean.getLeaderInfo().getLeaderBean().size() == 0) {
//            callBack.requestError(null);
//            return;
//        }
//        callBack.requestSuccess(leaderBean);


        Call<LeaderBean> call = retrofit.create(ApiService.class).getLeaderInfo("0245", loginId);
        call.enqueue(new Callback<LeaderBean>() {
            @Override
            public void onResponse(Call<LeaderBean> call, Response<LeaderBean> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(response.body());
            }

            @Override
            public void onFailure(Call<LeaderBean> call, Throwable t) {
                callBack.requestError(null);
            }
        });
    }

    /**
     * 获取优惠券数量
     *
     * @param loginId  登录id
     * @param callBack 回调
     */
    public void getCouponCount(String loginId, final ModelParamCallBack callBack) {
        Call<CouponCountBean> call = retrofit.create(ApiService.class).getCouponCount("0346", loginId);
        call.enqueue(new Callback<CouponCountBean>() {
            @Override
            public void onResponse(Call<CouponCountBean> call, Response<CouponCountBean> response) {
                CouponCountBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(body);
            }

            @Override
            public void onFailure(Call<CouponCountBean> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }

    public String parseToString(LinkedHashMap<String, Object> map) {
        StringBuilder params = new StringBuilder();
        params.append("{");
        for (String s : map.keySet()) {
            params.append("\"").append(s).append("\"").append(":\"").append(map.get(s)).append("\"").append(",");
        }
        params.deleteCharAt(params.lastIndexOf(","));
        params.append("}");
        return params.toString();
    }

    public void println(Object object) {
        Logger.i(TAG, object.toString());
    }

    public void printlnE(Object object) {
        Logger.e(TAG, object.toString());
    }

    public String getUid(Context context) {
        if (db == null) {
            db = DBManager.getInstance();
        }
        return db.GetLoginUid(context);
    }

    public String getImei(Context context) {
        if (dTag == null) {
            dTag = new DeviceTag(context);
        }
        return dTag.getImei();
    }

    public String getMac(Context context) {
        if (dTag == null) {
            dTag = new DeviceTag(context);
        }
        return dTag.getMac();
    }

    public String getDeviceID(Context context) {
        if (dTag == null) {
            dTag = new DeviceTag(context);
        }
        return dTag.getDeviceID();
    }

    public void sendMessage(Handler handler, int what, int arg1, Object object) {
        if (handler == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.obj = object;
        handler.sendMessage(message);
    }

    public void sendMessage(Handler handler, int what, int arg1) {
        sendMessage(handler, what, arg1, null);
    }

    public void sendMessage(Handler handler, int what, String info) {
        if (handler == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = info;
        handler.sendMessage(message);
    }

    public void sendMessage(Handler handler, int what, Object obj) {
        sendMessage(handler, what, -1, obj);
    }

    public void sendMessage(Handler handler, int what) {
        sendMessage(handler, what, null);
    }

}
