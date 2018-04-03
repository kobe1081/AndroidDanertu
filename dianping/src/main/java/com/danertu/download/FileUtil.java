package com.danertu.download;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.danertu.tools.Logger;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 开始消息提示常量
     */
    public static final int startDownloadMeg = 1;

    /**
     * 更新消息提示常量
     */
    public static final int updateDownloadMeg = 2;

    /**
     * 完成消息提示常量
     */
    public static final int endDownloadMeg = 3;

    /**
     * 检验SDcard状态
     *
     * @return boolean
     */
    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 保存文件文件到目录
     *
     * @param context
     * @return 文件保存的目录
     */
    public static String setMkdir(Context context) {
        String filePath;

        if (checkSDCard()) {
            filePath = Environment.getExternalStorageDirectory() + File.separator + "myfile";
        } else {
            filePath = context.getCacheDir().getAbsolutePath() + File.separator + "myfile";
        }
        File file = new File(filePath);
        if (!file.exists()) {
            boolean b = file.mkdirs();
            Log.e("file", "文件不存在  创建文件    " + b);
        } else {
            Log.e("file", "文件存在");
        }
        return filePath;
    }

    /**
     * 得到文件的名称
     *
     * @return
     * @throws IOException
     */
    public static String getFileName(String url) {
        String name = null;
        try {
            name = url.substring(url.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void deleteFiles(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFiles(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            // Constants.Logdada("文件不存在！"+"\n");
        }
    }
}
