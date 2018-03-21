package com.migu.data.android.logbase.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Created by hasika on 2017/9/21.
 *  文件相关工具类
 */
public class LogBaseUtil {
    public static byte[] getBytesFromInputStream(InputStream inputStream) {
        byte[] results = null;
        ByteArrayOutputStream arrayOutputStream = null;
        try {
            arrayOutputStream = new ByteArrayOutputStream();
            byte[] bytesArray = new byte[1024];
            int i;
            while (-1 != (i= inputStream.read(bytesArray))){
                arrayOutputStream.write(bytesArray, 0, i);
            }

            results = arrayOutputStream.toByteArray();
        }catch (Exception e) {
            LogBaseLog.w(e);
        } finally {
            close(arrayOutputStream);
        }

        return results;
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable !=null) {
                closeable.close();
            }
        }catch (Exception e) {
            LogBaseLog.w(e);
        }
    }

    public static String getDateTime(long millis) {
        Date _date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        return format.format(_date);
    }
}
