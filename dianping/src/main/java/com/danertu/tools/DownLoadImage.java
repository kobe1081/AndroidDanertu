package com.danertu.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;

    public DownLoadImage(ImageView imageView) {
        this.imageView = imageView;

    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap tmpBitmap = null;
        try {
            InputStream is = new java.net.URL(url).openStream();
            tmpBitmap = BitmapFactory.decodeStream(is);

            // 从网络上获取图片
            String name = url.substring(url.lastIndexOf("/") + 1); // 图片名称
            File picFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "danertu", name);// 仅创建路径的File对象
            // 从网络上获取图片
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {
                InputStream stream = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(picFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                stream.close();
                fos.close();

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return tmpBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);

    }


}
