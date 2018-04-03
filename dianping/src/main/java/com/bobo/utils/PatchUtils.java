package com.bobo.utils;


public class PatchUtils {

    public static native int patch(String oldApkPath, String newApkPath, String patchPath);
}