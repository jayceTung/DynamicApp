package com.asuper.dynamicapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Joker on 2016/7/29.
 */
public class Utils {

    public static boolean isSDExist() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getApkVersion (Context con, String apkPath) {
        int version = -1;
        try {
            PackageManager manager = con.getPackageManager();
            PackageInfo info = manager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            version = info.versionCode;
        } catch (Exception e) {
            Log.i("getApkVersion", Log.getStackTraceString(e));
        }
        return version;
    }
}
