package com.migu.data.android.logbase.save_log;

import com.migu.data.android.logbase.database.ILogBaseDbManager;
import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseUploadLogInterceptorChain;

/**
 * Created by hasika on 2017/9/22.
 * 数据库模块
 */

public class LogBaseInsertDbInterceptor implements ILogBaseInterceptor {

    private final ILogBaseDbManager mDbManager;

    public LogBaseInsertDbInterceptor(ILogBaseDbManager dbManager) {
        this.mDbManager = dbManager;
    }
    @Override
    public long intercept(IChain chain) {
        long _index = -1;
        if (chain!=null && chain instanceof LogBaseUploadLogInterceptorChain) {
            LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
            byte[] bytes = _chain.getBytes();
            _index = mDbManager.insertCommonLog(bytes);
        }

        return _index;
    }
}
