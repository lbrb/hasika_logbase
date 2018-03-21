package com.migu.data.android.logbase.save_log;

import com.migu.data.android.logbase.encrypt.ILogBaseEncrypt;
import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseUploadLogInterceptorChain;

import org.json.JSONObject;

/**
 * Created by hasika on 2017/9/21.
 * 加密拦截器
 */

public class LogBaseEncryptInterceptor implements ILogBaseInterceptor {
    private final ILogBaseEncrypt mEncrypt;
    public LogBaseEncryptInterceptor(ILogBaseEncrypt encrypt) {
        mEncrypt = encrypt;
    }

    @Override
    public long intercept(IChain chain) {
        long _index = -1;
        LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
        JSONObject _jsonObject = _chain.getJSONObject();
        if (_jsonObject != null) {
            byte[] bytes = mEncrypt.encrypt(_jsonObject.toString().getBytes());
            _index = _chain.process(_jsonObject, bytes);
        }
        return _index;
    }
}
