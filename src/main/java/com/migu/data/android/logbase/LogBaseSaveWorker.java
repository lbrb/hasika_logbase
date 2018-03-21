package com.migu.data.android.logbase;

import com.migu.data.android.logbase.database.ILogBaseDbManager;
import com.migu.data.android.logbase.encrypt.ILogBaseEncrypt;
import com.migu.data.android.logbase.save_log.LogBaseBase64Interceptor;
import com.migu.data.android.logbase.save_log.LogBaseEncryptInterceptor;
import com.migu.data.android.logbase.save_log.LogBaseInsertDbInterceptor;
import com.migu.data.android.logbase.thread.LogBaseThreadPool;
import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseUploadLogInterceptorChain;
import com.migu.data.android.logbase.util.LogBaseLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasika on 2017/9/20.
 * 保存数据
 */

public class LogBaseSaveWorker {
    private final ILogBaseEncrypt mEncrypt;
    private final ILogBaseDbManager mDbManager;

    private LogBaseUploadLogInterceptorChain mChain;

    LogBaseSaveWorker(ILogBaseDbManager dbManager, ILogBaseEncrypt encrypt) {
        mEncrypt = encrypt;
        mDbManager = dbManager;
    }

    public void saveLog(final JSONObject jsonObject) {
        LogBaseLog.d("saveLog:"+jsonObject.toString());
        LogBaseThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                saveLogWithInterceptorChain(jsonObject);
            }
        });
    }

    public void syncSaveLog(JSONObject jsonObject) {
        LogBaseLog.d("saveLog:"+jsonObject.toString());
        saveLogWithInterceptorChain(jsonObject);
    }

    private void saveLogWithInterceptorChain(JSONObject jsonObject) {
        if (mChain == null) {
            List<ILogBaseInterceptor> _list = new ArrayList<>();
            _list.add(new LogBaseEncryptInterceptor(mEncrypt));
            _list.add(new LogBaseBase64Interceptor());
            _list.add(new LogBaseInsertDbInterceptor(mDbManager));

            mChain = new LogBaseUploadLogInterceptorChain(_list);
        }
        mChain.process(jsonObject, null);
    }
}
