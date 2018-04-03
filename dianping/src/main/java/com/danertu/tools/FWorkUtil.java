package com.danertu.tools;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.danertu.download.FileUtil;

public class FWorkUtil {
    private Context context;

    public FWorkUtil(Context context) {
        this.context = context;
    }

    /**
     * 裁剪图片
     *
     * @param uri     需要裁剪的图片路径
     * @param width   裁剪后图片的宽
     * @param height  裁剪后图片的高
     * @param reqCode 请求码
     * @param isSave  是否保存图片到.../crop/..路径下
     * @return 返回图片保存的路径，图片没保存则返回null
     */
    public String startPhotoZoom(Uri uri, int width, int height, int reqCode, boolean isSave) {
        String fileName = isSave ? FileUtil.getFileName(uri.toString()) : null;
        return startPhotoZoom1(uri, width, height, reqCode, fileName);
    }

    public String startPhotoZoom1(Uri uri, int width, int height, int reqCode, String fileName) {
        String path = null;
        if (!TextUtils.isEmpty(fileName)) {
            path = initSavePath(fileName);
            startPhotoZoom(uri, width, height, reqCode, Uri.parse("file://" + path));
        } else {
            startPhotoZoom(uri, width, height, reqCode, null);
        }
        return path;
    }

    public String startPhotoZoom(Uri uri, int aspectX, int aspectY, int width, int height, int reqCode, boolean isSave) {
        String path = null;
        if (isSave) {
            path = initSavePath(uri);
            startPhotoZoom(uri, aspectX, aspectY, width, height, reqCode, Uri.parse("file://" + path));
        } else {
            startPhotoZoom(uri, aspectX, aspectY, width, height, reqCode, null);
        }
        return path;
    }

    private String initSavePath(Uri uri) {
        return initSavePath(FileUtil.getFileName(uri.toString()));
    }

    private String initSavePath(String fileName) {
        String path = FileUtil.setMkdir(context);
        path += "/crop";
        File file = new File(path);
        if (!file.isDirectory()) {
            boolean mkdir = file.mkdir();
        }
        path += "/" + fileName;
        return path;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri, int width, int height, int reqCode, Uri savePath) {
        startPhotoZoom(uri, 1, 1, width, height, reqCode, savePath);
    }

    public void startPhotoZoom(Uri uri, int aspectX, int aspectY, int width, int height, int reqCode, Uri savePath) {
        Logger.i("图片路径", uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边

        if (savePath == null) {
            // 是否以bitmap对象返回数据（bundle.getParcelable("data")的形式获得）
            intent.putExtra("return-data", true);
        } else {
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savePath);
        }
        ((Activity) context).startActivityForResult(intent, reqCode);
    }
}
