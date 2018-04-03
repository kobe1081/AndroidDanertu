package com.danertu.tools;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/***
 * DownUtil
 *
 * @author qifei
 *
 */
public class DownUtil {
    private static final int TIMEOUT = 10 * 1000;

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    public void downloadUpdateFile(String down_url, String file) throws Exception {
        int down_step = 5;// 提示step
        int totalSize;
        int downloadCount = 0;// 已经下载好的大小
        InputStream inputStream=null;
        OutputStream outputStream=null;

        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        try {
            inputStream = httpURLConnection.getInputStream();
            outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
            byte buffer[] = new byte[1024];
            int readSize = 0;
            while ((readSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readSize);
                totalSize += readSize;// 时时获取下载到的大小
                //
                if (downloadCount == 0 || (totalSize * 100 / totalSize - down_step) > downloadCount) {
                    //这。。。是想干嘛
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream!=null){
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
