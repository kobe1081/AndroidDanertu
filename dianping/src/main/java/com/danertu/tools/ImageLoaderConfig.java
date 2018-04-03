package com.danertu.tools;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;


import com.danertu.dianping.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class ImageLoaderConfig {

    /**
     * @param isShowDefault true：显示默认的加载图片 false：不显示默认的加载图
     * @return
     */
    public static DisplayImageOptions initDisplayOptions(boolean isShowDefault) {
        DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
        displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
        if (isShowDefault) {
            displayImageOptionsBuilder.showStubImage(R.drawable.no_image);
            displayImageOptionsBuilder.showImageForEmptyUri(R.drawable.no_image);
            displayImageOptionsBuilder.showImageOnFail(R.drawable.no_image);
        }
        displayImageOptionsBuilder.cacheInMemory(true);
        displayImageOptionsBuilder.cacheOnDisc(true);
        displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);

        return displayImageOptionsBuilder.build();
    }

    public static DisplayImageOptions initDisplayOptions(int targetWidth,
                                                         boolean isShowDefault) {
        DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
        displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
        if (isShowDefault) {
            displayImageOptionsBuilder.showStubImage(R.drawable.no_image);
            displayImageOptionsBuilder.showImageForEmptyUri(R.drawable.no_image);
            displayImageOptionsBuilder.showImageOnFail(R.drawable.no_image);
        }
        displayImageOptionsBuilder.cacheInMemory(true);
        displayImageOptionsBuilder.cacheOnDisc(true);
        // 设置图片的编码格式为RGB_565，此格式比ARGB_8888
        displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        // 设置图片显示方式
        displayImageOptionsBuilder.displayer(new SimpleImageDisplayer(targetWidth));

        return displayImageOptionsBuilder.build();
    }

    public static void initImageLoader(Context context, String cacheDisc) {
        initImageLoader(context, cacheDisc, true);
    }

    /**
     * 异步图片加载ImageLoader的初始化操作，在Application中调用此方法
     *
     * @param context
     * @param cacheDisc
     */
    public static void initImageLoader(Context context, String cacheDisc, boolean isShowDefault) {
        // 配置ImageLoader
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, cacheDisc);
        // 实例化Builder
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                context);
        // 设置线程数量
        builder.threadPoolSize(3);
        // 设定线程等级比普通低
        builder.threadPriority(Thread.NORM_PRIORITY);
        // 设定内存缓存为弱缓存
        builder.memoryCache(new WeakMemoryCache());
        // 设定内存图片缓存大小限制，不设置默认为屏幕的宽高
        builder.memoryCacheExtraOptions(480, 800);
        builder.denyCacheImageMultipleSizesInMemory();
        // 设定缓存的SDcard目录，UnlimitDiscCache速度
        builder.discCache(new UnlimitedDiscCache(cacheDir));
        // 设定缓存到SDCard目录的文件命
        builder.discCacheFileNameGenerator(new HashCodeFileNameGenerator());
        // 设定网络连接超时 timeout: 10s 读取网络连接超时read timeout: 60s
        builder.imageDownloader(new BaseImageDownloader(context, 10000, 60000));
        // 设置ImageLoader的配置参
        builder.defaultDisplayImageOptions(initDisplayOptions(isShowDefault));

        // 初始化ImageLoader
        ImageLoader.getInstance().init(builder.build());
    }
}
