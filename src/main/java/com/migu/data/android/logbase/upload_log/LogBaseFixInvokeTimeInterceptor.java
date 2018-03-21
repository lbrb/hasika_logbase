package com.migu.data.android.logbase.upload_log;

import com.migu.data.android.logbase.LogBaseConstant;
import com.migu.data.android.logbase.device.ILogBaseLocalStore;
import com.migu.data.android.logbase.device.LogBasePreferencesLocalStore;
import com.migu.data.android.logbase.util.LogBaseLog;
import com.migu.data.android.logbase.util.LogBaseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.migu.data.android.logbase.LogBaseConstant.Constant.END_TIME;
import static com.migu.data.android.logbase.LogBaseConstant.Constant.INVOKE_TIME;
import static com.migu.data.android.logbase.LogBaseConstant.Constant.START_TIME;

/**
 * Created by hasika on 2017/9/21.
 * 修复客户端时间
 */

public class LogBaseFixInvokeTimeInterceptor implements ILogBaseInterceptor {
    private final ILogBaseLocalStore mLocalStore;

    public LogBaseFixInvokeTimeInterceptor(ILogBaseLocalStore localStore) {
        mLocalStore = localStore;
    }

    @Override
    public long intercept(ILogBaseInterceptor.IChain chain) {
        //修复时间
        LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
        JSONObject _jsonObject = _chain.getJSONObject();
        fixInvokeTime(_jsonObject);

        //继续执行
        return chain.process(_jsonObject, null);
    }

    private void fixInvokeTime(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.length() > 0) {
            JSONArray _jsonArray = jsonObject.optJSONArray(LogBaseConstant.Constant.LOG_ARRAY);
            JSONObject _jsonObject;
            Object _object;
            for (int i = 0; i < _jsonArray.length(); i++) {
                _object = _jsonArray.opt(i);
                if (_object != null && _object instanceof JSONObject) {
                    _jsonObject = (JSONObject) _object;

                    try {

                        long invokeTimeL = _jsonObject.optLong(INVOKE_TIME);
                        if (invokeTimeL != 0) {
                            long correctInvokeTimeL = invokeTimeL + mLocalStore.getLong(LogBasePreferencesLocalStore.TIME_MILLIS_DIFF);
                            LogBaseLog.d("fixTime, invokeTimeL:" + invokeTimeL + ", correctInvokeTimeL:" + correctInvokeTimeL + ", correctTimeStr:" + LogBaseUtil.getDateTime(correctInvokeTimeL));
                            _jsonObject.putOpt(INVOKE_TIME, String.valueOf(correctInvokeTimeL));
                        }

                        long startTimeL = _jsonObject.optLong(START_TIME);
                        if (startTimeL != 0) {
                            long correctStartTimeL = startTimeL + mLocalStore.getLong(LogBasePreferencesLocalStore.TIME_MILLIS_DIFF);
                            LogBaseLog.d("fixTime, startTimeL:" + invokeTimeL + ", correctStartTimeL:" + correctStartTimeL + ", correctTimeStr:" + LogBaseUtil.getDateTime(correctStartTimeL));
                            _jsonObject.putOpt(START_TIME, String.valueOf(correctStartTimeL));
                        }


                        long endTimeL = _jsonObject.optLong(END_TIME);
                        if (endTimeL != 0) {
                            long correctEndTimeL = endTimeL + mLocalStore.getLong(LogBasePreferencesLocalStore.TIME_MILLIS_DIFF);
                            LogBaseLog.d("fixTime, endTimeL:" + invokeTimeL + ", correctEndTimeL:" + correctEndTimeL + ", correctTimeStr:" + LogBaseUtil.getDateTime(correctEndTimeL));
                            _jsonObject.putOpt(END_TIME, String.valueOf(correctEndTimeL));
                        }
                    } catch (JSONException e) {
                        LogBaseLog.w(e);
                    }

                }
            }
        }
    }
}
