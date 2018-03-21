package com.migu.data.android.logbase.save_log;

import android.util.Base64;

import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseUploadLogInterceptorChain;

/**
 * Created by hasika on 2017/9/22.
 * Base64处理
 */

public class LogBaseBase64Interceptor implements ILogBaseInterceptor {
    @Override
    public long intercept(IChain chain) {
        long _index = -1;
        if (chain!=null && chain instanceof LogBaseUploadLogInterceptorChain) {
            LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
            byte[] _bytes = _chain.getBytes();
            byte[] _base64Bytes = Base64.encode(_bytes, Base64.DEFAULT);
            _index = _chain.process(_chain.getJSONObject(), _base64Bytes);
        }
        return _index;
    }
}
