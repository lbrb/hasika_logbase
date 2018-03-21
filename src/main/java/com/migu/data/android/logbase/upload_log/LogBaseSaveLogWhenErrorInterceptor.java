package com.migu.data.android.logbase.upload_log;

import com.migu.data.android.logbase.LogBaseConstant;
import com.migu.data.android.logbase.LogBaseSaveWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hasika on 2017/12/4.
 *
 * 保存日志出错时的处理
 */

public class LogBaseSaveLogWhenErrorInterceptor implements ILogBaseInterceptor {

    private final LogBaseSaveWorker mSaveWorker;

    public LogBaseSaveLogWhenErrorInterceptor(LogBaseSaveWorker saveWorker) {
        mSaveWorker = saveWorker;
    }

    @Override
    public long intercept(IChain chain) {


        LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
        JSONObject jsonObject = _chain.getJSONObject();


        long result = 0;
        try {
            JSONObject _jsonObject = new JSONObject(jsonObject.toString());
            JSONObject _rootObject = new JSONObject();
            JSONArray _jsonArray = new JSONArray();
            _jsonArray.put(_jsonObject);
            _rootObject.put(LogBaseConstant.Constant.LOG_ARRAY, _jsonArray);

            result = _chain.process(_rootObject, null);
            //上传失败，保存本地
            if (result == -1){
                mSaveWorker.saveLog(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
