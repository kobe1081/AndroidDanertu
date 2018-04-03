package com.bobo.utils;

/**
 * @author Cundong
 * @version 1.0
 * @date 2013-9-6
 */
public class DiffUtils {

    /**
     * 本地方法 比较路径为oldPath的apk与newPath的apk之间差异，并生成patch包，存储于patchPath
     *
     * @param oldApkPath
     * @param newApkPath
     * @param patchPath
     * @return
     */
    public static native int genDiff(String oldApkPath, String newApkPath, String patchPath);
}