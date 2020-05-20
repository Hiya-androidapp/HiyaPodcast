package com.xmum.hiyapodcast.utils;

import android.util.Log;

<<<<<<< HEAD
=======
/**
 * Created by TrillGates on 18/10/7.
 * God bless my code!
 */
>>>>>>> b0a17fa9cf6a7ddc651a2a7f3c8d772f76574931
public class LogUtil {

    public static String sTAG = "LogUtil";

<<<<<<< HEAD
    //控制是否要输出log
    public static boolean sIsRelease = false;

    /**
     * 如果是要发布了，可以在application里面把这里release一下，这样子就没有log输出了
=======
    //control whether output log
    public static boolean sIsRelease = false;

    /**
     * when it will be released，can release in application first，so that no log output.
>>>>>>> b0a17fa9cf6a7ddc651a2a7f3c8d772f76574931
     */
    public static void init(String baseTag, boolean isRelease) {
        sTAG = baseTag;
        sIsRelease = isRelease;
    }

    public static void d(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void v(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void w(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }
}