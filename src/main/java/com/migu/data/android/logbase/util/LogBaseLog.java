package com.migu.data.android.logbase.util;

import android.util.Log;

/**
 * Created by hasika on 2017/9/19.
 * 日志
 */

public class LogBaseLog {
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    private static final String TAG = "LogBase";

    private static int sMinLevel = DEBUG;

    public static void d(String msg) {
        d(TAG, msg);
    }

    private static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    private static void d(String tag, String msg, Throwable tr) {
        if (shouldLog(DEBUG)){
            Log.d(tag,msg,tr);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    private static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    private static void i(String tag, String msg, Throwable tr) {
        if (shouldLog(INFO)){
            Log.i(tag,msg,tr);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    private static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    private static void w(String tag, String msg, Throwable tr) {
        if (shouldLog(WARN)){
            Log.w(tag,msg,tr);
        }
    }

    public static void w(Throwable tr) {
        Log.w("catched exeception", "catched exeception", tr);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    private static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    private static void e(String tag, String msg, Throwable tr) {
        if (shouldLog(ERROR)){
            Log.e(tag,msg,tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        e(TAG, msg, tr);
    }

    public static void e(Throwable tr) {
        e(TAG, "", tr);
    }

    private static boolean shouldLog(int level) {
        return sMinLevel <= level;
    }

    public static void setLogLevel(boolean isDebugMode) {
        if (isDebugMode) {
            sMinLevel = DEBUG;
        } else {
            sMinLevel = INFO;
        }
    }
}
