package com.migu.data.android.logbase;

import com.migu.data.android.logbase.device.ILogBaseLocalStore;
import com.migu.data.android.logbase.device.LogBasePreferencesLocalStore;
import com.migu.data.android.logbase.http.ILogBaseConnection;
import com.migu.data.android.logbase.http.LogBaseHttpResult;
import com.migu.data.android.logbase.http.LogBaseHttpURLConnection;
import com.migu.data.android.logbase.thread.LogBaseThreadPool;
import com.migu.data.android.logbase.util.LogBaseLog;

import org.json.JSONObject;

/**
 * Created by hasika on 2017/9/22.
 * 查询服务器信息类
 */

class LogBaseQueryServiceInfoWorker {
    private final String mQueryServiceInfoUrl;
    private final ILogBaseConnection mConnect;
    private final ILogBaseLocalStore mLocalStore;
    LogBaseQueryServiceInfoWorker(String queryServiceInfoUrl, ILogBaseLocalStore sharePreferences) {
        this.mQueryServiceInfoUrl = queryServiceInfoUrl;
        this.mConnect = new LogBaseHttpURLConnection();
        this.mLocalStore = sharePreferences;
    }

    /**
     * 查询服务器信息
     */
    public void query() {
        LogBaseThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                realQuery();
            }
        });
    }

    private void realQuery(){
        JSONObject _jsonObject = mConnect.request(mQueryServiceInfoUrl);
        String code = _jsonObject.optString("resultCode");
        JSONObject _resultObject = _jsonObject.optJSONObject("result");
        if (code.equals("200") && _resultObject!=null){
            long _serverTimeMillis = _resultObject.optLong(LogBaseHttpResult.QUERY_SERVER_INFO.SERVER_TIME);
            if (_serverTimeMillis != 0) {
                long _currrentTimeMillis = System.currentTimeMillis();
                long _timeDiff = _serverTimeMillis - _currrentTimeMillis;
                mLocalStore.putLong(LogBasePreferencesLocalStore.TIME_MILLIS_DIFF, _timeDiff);
            } else {
                LogBaseLog.e("查询服务器信息失败，无时间字段");
            }
        } else {
            LogBaseLog.e("查询服务器信息失败");
        }
    }
}
