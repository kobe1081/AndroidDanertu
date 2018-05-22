package com.danertu.base;

import android.content.Context;
import android.database.Cursor;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
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
                //在这里获取到request后就可以做任何事情了
                RequestBody requestBody = request.body();
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(Charset.forName("utf-8"));
                }
                String paramsStr = buffer.readString(charset);
                Logger.d(TAG, request.url() + "\n" + paramsStr);
                okhttp3.Response response = chain.proceed(request);
                return response;
            }
        };
        builder.addInterceptor(myInterceptor);
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Constants.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
}
