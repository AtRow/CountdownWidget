package com.ovio.countdown.log;

import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * Countdown
 * com.ovio.countdown.log
 */
public final class Logger {

    public static final String PREFIX = "CW_";

    public static final boolean DEBUG = true;

    private static final String TAG = PREFIX + "Log";

    public static void v(String tag, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.VERBOSE)) Log.v(tag, getLocation() + getMessage(msg, params));
    }

    public static void v(String tag, Throwable tr, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.VERBOSE)) Log.v(tag, getLocation() + getMessage(msg, params), tr);
    }

    public static void d(String tag, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.DEBUG)) Log.d(tag, getLocation() + getMessage(msg, params));
    }

    public static void d(String tag, Throwable tr, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.DEBUG)) Log.d(tag, getLocation() + getMessage(msg, params), tr);
    }

    public static void i(String tag, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.INFO)) Log.i(tag, getLocation() + getMessage(msg, params));
    }

    public static void i(String tag, Throwable tr, String msg, Object... params) {
        if (DEBUG && Log.isLoggable(tag, Log.INFO)) Log.i(tag, getLocation() + getMessage(msg, params), tr);
    }

    public static void w(String tag, String msg, Object... params) {
        if (Log.isLoggable(tag, Log.WARN)) Log.w(tag, getLocation() + getMessage(msg, params));
    }

    public static void w(String tag, Throwable tr, String msg, Object... params) {
        if (Log.isLoggable(tag, Log.WARN)) Log.w(tag, getLocation() + getMessage(msg, params), tr);
    }

    public static void w(String tag, Throwable tr) {
        if (Log.isLoggable(tag, Log.WARN)) Log.w(tag, tr);
    }

    public static void e(String tag, String msg, Object... params) {
        if (Log.isLoggable(tag, Log.ERROR)) Log.e(tag, getLocation() + getMessage(msg, params));
    }

    public static void e(String tag, Throwable tr, String msg, Object... params) {
        if (Log.isLoggable(tag, Log.ERROR)) Log.e(tag, getLocation() + getMessage(msg, params), tr);
    }


    private static String getMessage(String msg, Object... params) {
        try {
            return String.format(Locale.US, msg, params);
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to format log message", e);
            return "Failed to format [" + msg + "]";
        }
    }

    private static String getLocation() {
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            }
            catch (ClassNotFoundException e) {
                return "[ClassNotFoundException]: ";
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

}
