package com.danertu.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public class DownLoad {


    public void downLoad(String url) {
        InputStream stream=null;
        FileOutputStream fos=null;
        try {
            // 从网络上获取图片
            String name = url.substring(url.lastIndexOf("/") + 1); // 图片名称
            File picFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "CACHE", name);// 仅创建路径的File对象
            // 从网络上获取图片
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {
                 stream = conn.getInputStream();
                 fos = new FileOutputStream(picFile);
                byte[] buffer = new byte[1024];
                int len=-1;
                while ((len = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(stream!=null){
                    stream.close();
                }
                if (fos!=null){
                    fos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }


}
