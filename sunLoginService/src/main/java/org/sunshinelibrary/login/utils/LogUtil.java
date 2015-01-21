package org.sunshinelibrary.login.utils;

import android.util.Log;

/**
 * Copyright (c) 2014 Guanghe.tv All right reserved.
 * --------------------<-.->-----------------------
 * Author:      Nono(陈凯)
 * CreateDate:  14/12/24
 * Description: exp...
 * Version:     1.0.0
 */
public class LogUtil {


    private final static String LOG_TAG = "LILITH";
    private static boolean IS_OUTPUT = true;

    /**
     * 输出log操作
     *
     * @param key   key值
     * @param value 输出值
     * @since 1.0.0
     */
    public static void log(String key, String value) {

        StringBuilder builder = new StringBuilder();
        builder.append(key);
        builder.append("====");
        builder.append(value);
        log(builder.toString());
    }


    /**
     * 输出log操作
     *
     * @param msg msg
     * @since 1.0.0
     */
    public static void log(String msg) {
        if (IS_OUTPUT && msg != null) {
            Log.i(LOG_TAG, msg);
        }

    }

}
