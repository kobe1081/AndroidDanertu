package com.danertu.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;

import com.danertu.entity.BaseMessage;
import com.danertu.entity.BaseModel;

public class AppUtil {

    /* md5 加密 */
    static public String md5(String str) {
        MessageDigest algorithm = null;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (algorithm != null) {
            algorithm.reset();
            algorithm.update(str.getBytes());
            byte[] bytes = algorithm.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                hexString.append(Integer.toHexString(0xFF & b));
            }
            return hexString.toString();
        }
        return "";

    }

    /* 首字母大写 */
    static public String ucFirst(String str) {
        if (str != null && !Objects.equals(str, "")) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    /* 为 EntityUtils.toString() 添加 gzip 解压功能 */
    public static String gzipToString(final HttpEntity entity, final String defaultCharset) throws IOException, ParseException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream inStream = entity.getContent();
        if (inStream == null) {
            return "";
        }
        // gzip logic start
        if (entity.getContentEncoding().getValue().contains("gzip")) {
            inStream = new GZIPInputStream(inStream);
        }
        // gzip logic end
        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
        }
        int i = (int) entity.getContentLength();
        if (i < 0) {
            i = 4096;
        }
        String charset = EntityUtils.getContentCharSet(entity);
        if (charset == null) {
            charset = defaultCharset;
        }
        if (charset == null) {
            charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        CharArrayBuffer buffer = new CharArrayBuffer(i);
        try (Reader reader = new InputStreamReader(inStream, charset)) {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        }
        return buffer.toString();
    }

    /* 为 EntityUtils.toString() 添加 gzip 解压功能 */
    public static String gzipToString(final HttpEntity entity)
            throws IOException, ParseException {
        return gzipToString(entity, null);
    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("com.app.demos.sp.global", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences(Service service) {
        return service.getSharedPreferences("com.app.demos.sp.global", Context.MODE_PRIVATE);
    }

    /////////////////////////////////////////////////////////////////////////////////
    // 业务逻辑

    /* 获取 Session Id */
    static public String getSessionId() {
//		Customer customer = Customer.getInstance();
//		return customer.getSid();
        return "";
    }

    /* 获取 Message */
    static public BaseMessage getMessage(String jsonStr, String jsonKey) throws Exception {
        BaseMessage message = new BaseMessage();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            if (jsonObject != null) {
                message.setResult(jsonObject.getString(jsonKey));
            }
        } catch (JSONException e) {
            throw new Exception("Json format error");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /* Model 数组转化成 Map 列表 */
    static public List<? extends Map<String, ?>> dataToList(List<? extends BaseModel> data, String[] fields) {
        ArrayList<HashMap<String, ?>> list = new ArrayList<>();
        for (BaseModel item : data) {
            list.add((HashMap<String, ?>) dataToMap(item, fields));
        }
        return list;
    }

    /* Model 转化成 Map */
    private static Map<String, ?> dataToMap(BaseModel data, String[] fields) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            for (String fieldName : fields) {
                Field field = data.getClass().getDeclaredField(fieldName);
                field.setAccessible(true); // have private to be accessable
                map.put(fieldName, field.get(data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /* 判断int是否为空 */
    static public boolean isEmptyInt(int v) {
        return new Integer(v) == null;
    }

    /* 获取毫秒数 */
    public static long getTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String getFormatTimeStamp() {
//		long timeSt = System.currentTimeMillis();
//		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//		String order = format.format(new Date(timeSt));
//		return order;
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    /* 获取耗费内存 */
    public static long getUsedMemory() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        return total - free;
    }

    public static int dip2px(Context context, float dipValue) {
        final float dpi = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (dipValue * (dpi / 160));
    }
}