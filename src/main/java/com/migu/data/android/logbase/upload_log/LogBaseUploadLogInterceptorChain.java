package com.migu.data.android.logbase.upload_log;

import com.migu.data.android.logbase.util.LogBaseLog;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by hasika on 2017/9/21.
 * 上传日志
 */

public class LogBaseUploadLogInterceptorChain implements ILogBaseInterceptor.IChain {
    private final List<ILogBaseInterceptor> mList;
    private int mIndex = 0;
    private final JSONObject mJSONObject;
    private final byte[] mBytes;
    private LogBaseUploadLogInterceptorChain(List<ILogBaseInterceptor> list, int index, JSONObject jsonObject, byte[] bytes) {
        this.mList = list;
        this.mIndex = index;
        this.mJSONObject = jsonObject;
        this.mBytes = bytes;
    }

    public LogBaseUploadLogInterceptorChain(List<ILogBaseInterceptor> list) {
        this(list, 0, null, null);
    }

    @Override
    public long process(JSONObject jsonObject, byte[] bytes) {
        long _count = 0;
        if (mList != null && mIndex <= mList.size()) {
            ILogBaseInterceptor _interceptor = mList.get(mIndex);
            LogBaseUploadLogInterceptorChain _chain = new LogBaseUploadLogInterceptorChain(mList, mIndex+1, jsonObject, bytes);
            LogBaseLog.d(_interceptor.getClass().getSimpleName() + "intercept");
            _count = _interceptor.intercept(_chain);
        }

        return _count;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public byte[] getBytes() {
        return mBytes;
    }
}
