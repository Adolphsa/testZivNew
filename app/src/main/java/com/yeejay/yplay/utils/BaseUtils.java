package com.yeejay.yplay.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/10/30.
 */

public class BaseUtils {

    public static File getAppRootPath(Context context) {
        if (sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().canWrite();
        } else
            return false;
    }

}
