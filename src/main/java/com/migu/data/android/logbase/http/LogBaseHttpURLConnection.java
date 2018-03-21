package com.migu.data.android.logbase.http;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hasika on 2017/9/21.
 * 上传日志
 */

public class LogBaseHttpURLConnection extends AbsLogBaseHttpConnection{

    @Override
    protected HttpURLConnection createUrlConnection(String url) {
        HttpURLConnection connection = null;
        try {
            URL _url = new URL(url);
            connection = (HttpURLConnection) _url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
