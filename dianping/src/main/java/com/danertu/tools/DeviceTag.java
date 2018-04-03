package com.danertu.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.danertu.download.FileUtil;
import com.google.gson.Gson;

public class DeviceTag {
    private Context context;
    private Gson gson;
    private static String imei = "";
    private static String deviceid = "";
    private static String mac = "";
    private String uuid = "";

    private final String key_imei = "imei";
    private final String key_mac = "mac";
    private final String key_deviceid = "deviceid";

    public DeviceTag(Context context) {
        this(context, new Gson());
    }

    public DeviceTag(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    public void init() {
        String path = FileUtil.setMkdir(context);
        path += "/.system";
        File tagFile = new File(path);
        AESEncrypt aes = null;
        try {
            aes = new AESEncrypt();
            aes.setAlgorithKey();
            initUUID(aes);

            long len = tagFile.length();
            boolean isFile = tagFile.isFile();
            if (isFile && len > 0) {
                String encStr = getFileContent(tagFile);
                String json = aes.decrypt(encStr);
                analysisJson(json);
            } else {
                saveData(aes, tagFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                saveData(aes, tagFile);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initUUID(AESEncrypt aes) {
        String filePath;
        if (FileUtil.checkSDCard()) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            filePath = context.getCacheDir().getAbsolutePath();
        }
        File fuuid = new File(filePath + "/.uuid");
        try {
            long len = fuuid.length();
            boolean isFile = fuuid.isFile();
            if (isFile && len > 0) {
                String encStr = getFileContent(fuuid);
                uuid = aes.decrypt(encStr);
            } else {
                String uuid = UUID.randomUUID().toString();
                saveToFile(fuuid, aes, uuid);
            }
        } catch (Exception e) {
            try {
                String uuid = UUID.randomUUID().toString();
                saveToFile(fuuid, aes, uuid);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void saveToFile(File file, AESEncrypt aes, String content) throws Exception {
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(aes.encrypt(content).getBytes(Charset.forName("UTF-8")));
        fout.flush();
        fout.close();
    }

    private String getFileContent(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(fin.available());
        byte buffer[] = new byte[1024];
        int s = 0;
        while ((s = fin.read(buffer)) != -1) {
            baos.write(buffer, 0, s);
        }
        fin.close();
        return new String(baos.toByteArray());
    }

    private void saveData(AESEncrypt aes, File tagFile) throws Exception {
        HashMap<String, String> tag = new HashMap<>();
        imei = getImei("");
        mac = getMac("");
        deviceid = getDeviceID("");
        tag.put(key_imei, imei);
        tag.put(key_mac, mac);
        tag.put(key_deviceid, deviceid);
        String dJson = gson.toJson(tag);
        saveToFile(tagFile, aes, dJson);
        setDefault(uuid);
    }

    private void analysisJson(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        imei = obj.getString(key_imei);
        mac = obj.getString(key_mac);
        deviceid = obj.getString(key_deviceid);

        setDefault(uuid);
    }

    private void setDefault(String uuid) {
        imei = TextUtils.isEmpty(imei) ? uuid : imei;
        mac = TextUtils.isEmpty(mac) ? uuid : mac;
        deviceid = TextUtils.isEmpty(deviceid) ? uuid : deviceid;
    }

    public String getImei() {
        if (TextUtils.isEmpty(imei)) {
            init();
        }
        return imei;
    }

    public String getMac() {
        if (TextUtils.isEmpty(mac)) {
            init();
        }
        return mac;
    }

    public String getDeviceID() {
        if (TextUtils.isEmpty(deviceid)) {
            init();
        }
        return deviceid;
    }

    private String getImei(String defaultStr) {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Log.e(context.getPackageName(), e.getMessage());
        }
        imei = TextUtils.isEmpty(imei) ? defaultStr : imei;
        return imei;
    }

    private String getMac(String defaultStr) {
        String macSerial = "";
        String str = "";
        InputStreamReader ir = null;
        LineNumberReader input = null;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            ir = new InputStreamReader(pp.getInputStream());
            input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        } finally {

            try {
                if (ir != null) {
                    ir.close();
                }
                if (input!=null){
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        macSerial = TextUtils.isEmpty(macSerial) ? defaultStr : macSerial;
        return macSerial;
    }

    private String getDeviceID(String defaultStr) {
        String id = "";
        try {
            id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        id = TextUtils.isEmpty(id) ? defaultStr : id;
        return id;
    }

}
