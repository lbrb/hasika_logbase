package com.migu.data.android.logbase.upload_log;

import android.text.TextUtils;

import com.migu.data.android.logbase.LogBaseConstant;
import com.migu.data.android.logbase.http.ILogBaseConnection;
import com.migu.data.android.logbase.http.LogBaseHttpURLConnection;
import com.migu.data.android.logbase.http.LogBaseHttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hasika on 2017/9/21.
 * 网络发送模块
 */

public class LogBaseHttpInterceptor implements ILogBaseInterceptor {
    private final String mUrl;
    private final String mSecondUrl;

    public LogBaseHttpInterceptor(String url, String secondUrl) {
        this.mUrl = url;
        this.mSecondUrl = secondUrl;
    }
    @Override
    public long intercept(IChain chain) {
        long _count = -1;
        LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
        byte[] _bytes = _chain.getBytes();

        JSONObject jsonObject = _chain.getJSONObject();
        if (jsonObject!=null) {
            JSONArray jsonArray = jsonObject.optJSONArray(LogBaseConstant.Constant.LOG_ARRAY);
            if (jsonArray != null) {
                _count = jsonArray.length();
            }
        }

        if (_count < 1) {
            return -1;
        }

        ILogBaseConnection connection;
        ILogBaseConnection secondConnection = null;

        connection = createConnection(mUrl);
        if (!TextUtils.isEmpty(mSecondUrl)) {
            secondConnection = createConnection(mSecondUrl);
        }

        JSONObject _resultJsonObject = null;

        if (connection != null) {
            _resultJsonObject = connection.request(_bytes, mUrl);
        }

        String code = "";
        if (_resultJsonObject != null) {
            code = _resultJsonObject.optString("resultCode");
        }

        if (TextUtils.isEmpty(code) && secondConnection != null) {
            _resultJsonObject = secondConnection.request(_bytes, mSecondUrl);
        }

        if (_resultJsonObject != null) {
            code = _resultJsonObject.optString("resultCode");
        }

        if (TextUtils.isEmpty(code)) {
            _count = -1;
        }

        return _count;
    }

    private ILogBaseConnection createConnection(String url) {
        ILogBaseConnection connection = null;
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("https")) {
                connection = new LogBaseHttpsURLConnection();
            } else if (url.startsWith("http")) {
                connection = new LogBaseHttpURLConnection();
            }
        }

        return connection;
    }
}
