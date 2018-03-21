package com.migu.data.android.logbase.http;

import com.migu.data.android.logbase.util.LogBaseLog;
import com.migu.data.android.logbase.util.LogBaseUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

/**
 * Created by hasika on 2017/11/6.
 *
 * 网络传输抽象类
 */

public abstract class AbsLogBaseHttpConnection implements ILogBaseConnection {
    private HttpURLConnection connection;

    @Override
    public JSONObject request(String url) {
        InputStream inputStream = null;
        byte[] result;
        String resultStr = null;
        JSONObject resultJson =  new JSONObject();
        try {
            connection = createUrlConnection(url);

            connection.setRequestProperty("X-MG-UTC", String.valueOf(System.currentTimeMillis()));
            connection.setRequestProperty("Msg-Type", "envelope/json");
            connection.setRequestProperty("Content-Type", "envelope/json");
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Response-Type", "json");
            connection.setRequestProperty("dataOnly", "true");

            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            int responseCode = connection.getResponseCode();


            resultJson.put("resultCode", responseCode);

            inputStream = connection.getInputStream();
            result = LogBaseUtil.getBytesFromInputStream(inputStream);
            if (result != null && result.length > 0) {
                resultStr = new String(result, Charset.forName("utf-8"));
                JSONObject _resultJson = new JSONObject(resultStr);
                resultJson.put("result", _resultJson);
            }
        } catch (Exception e) {
            LogBaseLog.w(e);
        } finally {
            LogBaseUtil.close(inputStream);
            LogBaseLog.d("http url:" + url);
            LogBaseLog.d("http resultStr: " + resultStr);
            LogBaseLog.d("http resultJson: " + resultJson);
            LogBaseLog.d("http thread: " + Thread.currentThread().getName());
            if (connection != null) {
                connection.disconnect();
            }
        }

        return resultJson;
    }

    @Override
    public JSONObject request(byte[] bytes, String url) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        byte[] result;
        String resultStr = null;
        JSONObject resultJson =  new JSONObject();
        try {
            connection = createUrlConnection(url);

            connection.setRequestProperty("X-MG-UTC", String.valueOf(System.currentTimeMillis()));
            connection.setRequestProperty("Msg-Type", "envelope/json");
            connection.setRequestProperty("Content-Type", "envelope/json");
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Response-Type", "json");
            connection.setRequestProperty("dataOnly", "true");

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            if (bytes != null && bytes.length > 0) {
                outputStream = connection.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();


            resultJson.put("resultCode", responseCode);

            inputStream = connection.getInputStream();
            result = LogBaseUtil.getBytesFromInputStream(inputStream);
            if (result != null && result.length > 0) {
                resultStr = new String(result, Charset.forName("utf-8"));
                JSONObject _resultJson = new JSONObject(resultStr);
                resultJson.put("result", _resultJson);
            }
        } catch (Exception e) {
            LogBaseLog.w(e);
        } finally {
            LogBaseUtil.close(outputStream);
            LogBaseUtil.close(inputStream);
            LogBaseLog.d("http url:" + url);
            LogBaseLog.d("http resultStr: " + resultStr);
            LogBaseLog.d("http resultJson: " + resultJson);
            LogBaseLog.d("http thread: " + Thread.currentThread().getId()+Thread.currentThread().getName());
            if (connection != null) {
                connection.disconnect();
            }
        }

        return resultJson;
    }

    protected abstract HttpURLConnection createUrlConnection(String url);
}
