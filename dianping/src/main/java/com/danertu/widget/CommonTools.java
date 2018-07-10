package com.danertu.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danertu.dianping.R;
import com.danertu.dianping.SplashActivity;
import com.danertu.tools.Logger;

public class CommonTools {
    public static final String PREFS_NAME = "JPUSH_EXAMPLE";
    public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
    public static final String PREFS_START_TIME = "PREFS_START_TIME";
    public static final String PREFS_END_TIME = "PREFS_END_TIME";
    public static final String KEY_APP_KEY = "JPUSH_APPKEY";

    public static boolean isEmpty(String s) {
        return null == s || s.length() == 0 || s.trim().length() == 0;
    }

    /**
     * 保留两位小数字符串
     */
    @SuppressLint("DefaultLocale")
    public static String formatZero2Str(double num) {
        return String.format("%.2f", num);
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appKey;
    }

    /**
     * 取得版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return manager.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    /**
     * 取得版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return manager.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 显示提示信息
     *
     * @param context
     * @param message
     */
    public static Toast toast;

    public static void showShortToast(final Context context,
                                      final String message) {
//		Logger.e("test","CommonTools showShortToast MESSAGE="+MESSAGE);
        if (toast == null) {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
                    TextView text = (TextView) view.findViewById(R.id.toast_message);
                    text.setText(message);
                    toast = new Toast(context);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                    toast.setView(view);
                    toast.show();
                    Looper.loop();
                }
            }).start();
        }else {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
                    TextView text = (TextView) view.findViewById(R.id.toast_message);
                    text.setText(message);
                    toast.setView(view);
                    toast.show();
                    Looper.loop();
                }
            }).start();
        }

    }

    /**
     * 把dip 转换为 px 单位数值
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 把px 转换为 dip 单位数值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 判断是否为正确号码
     */
    public static boolean isMobileNO(String mobiles) {
        boolean isPhoneNum = false;
//        String regular = "^(0|86|17951)?(13[0-9]|14[57]|15[^4,\\D]|16[0-9]|17[4678]|18[0-9])[0-9]{8}$";//大陆号码
//        String regularAomen = "^(853|852)?(28[0-9]{2}|((6|8)[0-9]{3}))[0-9]{4}$";//澳门号码
//        String regularHongkong = "^(886)?[1-9]{2}[-_－—]?[0-9]{3}[-_－—]?[0-9]{3}$";//香港号码
//        String regularTaiwan = "^(886)?[1-9]{1}[1-9]{2}[-_－—]?[0-9]{3}[-_－—]?[0-9]{3}$";//台湾号码
//        String[] regs = {regular, regularAomen, regularHongkong, regularTaiwan};


        //移动
        //134 135 136 137 138 139 147 150 151 152 157 158 159 172 178 182 183 184 187 188 198
        String cnMob="^((13[4-9])|147|(15[0-2,7-9])|178|(18[2-4,7,8])|198)\\d{8}$";
        //联通
        //130 131 132 145 155 156 166 171 175 176 185 186
        String cnUn="^((13[0-2])|145|(15[5,6])|166|(17[5,6])|(18[5,6]))\\d{8}$";
        //电信
        //133 149 153 173 177 180 181 189 199
        String cnNet="^(133|149|153|(17[3,4])|177|(18[0,1,9])|199)\\d{8}$";
        //# 虚拟运营商
        //# 电信 - 1700 1701 1702
        //# 移动 - 1703 1705 1706
        //# 联通 - 1704 1707 1708 1709 171
        String cnV="^((170[0-9])\\d{7})|171\\d{8}$";
        //港澳台
//        String cnHAT="^((((0?)|((00)?))(((\\s){0,2})|([-_－—\\s]?)))|([+]?))((853)|(852))()?([]?)([-_－—\\s]?)(28[0-9]{2}|((6|8)[0-9]{3}))[-_－—\\s]?[0-9]{4}$";
//        String[] regs = {cnMob, cnUn, cnNet, cnV,cnHAT};
        String[] regs = {cnMob, cnUn, cnNet, cnV};

        for (String item : regs) {
            if ((isPhoneNum = Pattern.compile(item).matcher(mobiles).matches()))
                break;
        }
        return isPhoneNum;
    }

    /**
     * 上传文件
     *
     * @param context
     * @param actionUrl
     * @param picName
     * @param upBitmap
     * @param param
     * @param quality
     * @return
     */
    public static boolean uploadFile(Context context, final String actionUrl, String picName, Bitmap upBitmap, HashMap<String, String> param, int quality) {
        String end = "\r\n";
        String PREFIX = "--";
        String boundary = "*****";
        String newName = picName;

        try {

            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            //以下是用于上传参数
            if (param != null && param.size() > 0) {
                Iterator<String> it = param.keySet().iterator();
                StringBuffer sb;
                while (it.hasNext()) {
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = param.get(key);
                    sb.append(PREFIX).append(boundary).append(end);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(end).append(end);
                    sb.append(value).append(end);
                    String params = sb.toString();
                    ds.write(params.getBytes());
                }
            }
            ds.writeBytes(PREFIX + boundary + end);

            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);

            ds.writeBytes(end);

            /* 取得文件的FileInputStream */
            // FileInputStream fStream = new FileInputStream(uploadFile);
            // 测试
            // String uploadFile =
            // "/storage/sdcard0/0123单耳兔美食zip/mobile web/IMAGES/cake/6.jpg";
            // Bitmap bitmap = BitmapFactory.decodeFile(uploadFile);
            // Bitmap bmpCompressed = Bitmap.createScaledBitmap(bitmap, 300,
            // 300, true);

            Bitmap bmpCompressed;
            if (!isWifiConnected(context) && quality == 0) {
                bmpCompressed = zoomBitmap(upBitmap, 300, 300);
            } else {
                bmpCompressed = upBitmap;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            // CompressFormat set up to JPG, you can change to PNG or whatever
            // you want;
            quality = quality == 0 ? 90 : quality;
            bmpCompressed.compress(CompressFormat.JPEG, quality, bos);
            byte[] data = bos.toByteArray();

            ByteArrayInputStream fStream = new ByteArrayInputStream(data);

            /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(PREFIX + boundary + PREFIX + end);

            /* close streams */
            fStream.close();
            ds.flush();

            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            /* 将Response显示于Dialog */
            // showDialog("上传成功" + b.toString().trim());
            Logger.i("上传成功", newName + " , " + data.length + " , " + b.toString());
            /* 关闭DataOutputStream */
            ds.close();
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // showDialog("上传失败" + e);
            Logger.i("上传失败", e.toString());
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    /**
     * 等比例缩放图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null)
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        float scale = scaleWidth <= scaleHeight ? scaleWidth : scaleHeight;
        matrix.postScale(scale, scale);
        Bitmap newBmp = null;
        try {
            newBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            newBmp = bitmap;
            System.gc();
        }
        return newBmp;
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void addMoneyLimit(final EditText et) {
        if (et == null)
            return;
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        et.setText(s);
                        et.setSelection(s.length());
                    }
                }
                if (s.toString().trim().equals(".")) {
                    s = "0" + s;
                    et.setText(s);
                    et.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et.setText(s.subSequence(0, 1));
                        et.setSelection(1);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void createShortCut(Context context) {
        //创建快捷方式的Intent
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortCutIntent.putExtra("duplicate", false);
        //需要显示的名称
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.icon);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        //点击快捷图片，运行的程序主入口
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context, SplashActivity.class));
        //发送广播。OK
        context.sendBroadcast(shortCutIntent);
    }

    /**
     * 中文字符串转Unicode码
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\\\u" + hexB;
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    /**
     * Unicode码转中文字符串
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);

        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);

                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;

                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);

                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);

                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';

                    else if (aChar == 'n')
                        aChar = '\n';

                    else if (aChar == 'f')
                        aChar = '\f';

                    outBuffer.append(aChar);
                }

            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }

    public static boolean isAppRunning(ActivityManager am, String packageName) {
        List<RunningTaskInfo> runActs = am.getRunningTasks(30);
        for (RunningTaskInfo info : runActs) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppIsInBackground(ActivityManager am, String packageName) {
        boolean isInBackground = true;
        if (Build.VERSION.SDK_INT > 20) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(packageName)) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(packageName)) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static int mul(String d1, String d2) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.multiply(b2).intValue();

    }

    public static LinkedHashMap<String, String> handleParamStr(String paramStr) {
        if (TextUtils.isEmpty(paramStr)) {
            return null;
        }
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        String[] strList = paramStr.split(",;");
        for (String aStrList : strList) {
            param.put(aStrList.substring(0, aStrList.indexOf("|")), aStrList.substring(aStrList.indexOf("|") + 1));
        }
        return param;
    }

    @SuppressLint("SimpleDateFormat")
    public static int daysBetween(String startDate, String endDate) {
        String format = startDate.contains("/") ? "yyyy/MM/dd" : "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;
        try {
            cal.setTime(sdf.parse(startDate));
            time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(endDate));
            time2 = cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            boolean result = realSize.y != size.y;
            return realSize.y != size.y;
        } else {

            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 比较两个日期,如果日期相等返回0,小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(String date1, String date2) {
        try {
            SimpleDateFormat sdf;
            if (date1.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            } else if (date1.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }

            //将日期转成Date对象作比较
            Date fomatDate1 = sdf.parse(date1);
            Date fomatDate2 = sdf.parse(date2);

            return fomatDate2.compareTo(fomatDate1);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
