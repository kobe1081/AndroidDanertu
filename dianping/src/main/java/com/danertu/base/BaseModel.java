package com.danertu.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.config.ApiService;
import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.CouponCountBean;
import com.danertu.entity.LeaderBean;
import com.danertu.entity.ShopStateBean;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
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

    public BaseModel() {
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

                RequestBody requestBody = request.body();
                if (requestBody != null && Constants.isDebug) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = Charset.forName("UTF-8");
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName("utf-8"));
                    }
                    paramsStr = buffer.readString(charset);
                    Logger.i(TAG, "\n" + request.url() + "\n" + paramsStr);
                }


                return chain.proceed(request);
            }

        };
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Logger.i(TAG, "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(loggingInterceptor)
                .addInterceptor(myInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS);//设置超时时间为10s

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Constants.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 检查用户是否已开店
     *
     * @param loginId
     * @param callBack
     */
    public void checkIsOpenShop(String loginId, final ModelParamCallBack callBack) {
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
     * @param loginId 登录id
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
        if (handler==null){
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = object;
        message.arg1 = arg1;
        handler.sendMessage(message);
    }

    public void sendMessage(Handler handler, int what, int arg1) {
        sendMessage(handler, what, arg1, null);
    }

    public void sendMessage(Handler handler, int what, Object obj) {
        sendMessage(handler, what, -1, obj);
    }

    public void sendMessage(Handler handler,int what){
        sendMessage(handler,what,null);
    }

}
