package com.migu.data.android.logbase.upload_log;

import android.database.Cursor;
import android.util.Base64;

import com.migu.data.android.logbase.LogBaseConstant;
import com.migu.data.android.logbase.database.ILogBaseDbManager;
import com.migu.data.android.logbase.database.LogBaseTableInfo;
import com.migu.data.android.logbase.encrypt.ILogBaseEncrypt;
import com.migu.data.android.logbase.util.LogBaseLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by hasika on 2017/9/21.
 * 数据库处理
 */

public class LogBaseDealLogFromDataBaseInterceptor implements ILogBaseInterceptor {
    //本次删除log的开始下标
    private int mStartIndex = 0;
    //本次删除log的结束下标
    private int mEndIndex = 0;
    //加解密对象
    private final ILogBaseEncrypt mEncrypt;
    //数据库对象
    private final ILogBaseDbManager mDbManager;

    public LogBaseDealLogFromDataBaseInterceptor(ILogBaseEncrypt encrypt, ILogBaseDbManager dbManager) {
        mEncrypt = encrypt;
        mDbManager = dbManager;
    }

    @Override
    public long intercept(IChain chain) {
        long _count = 0;

        LogBaseUploadLogInterceptorChain _chain = (LogBaseUploadLogInterceptorChain) chain;
        //查询数据
        JSONObject jsonObject = getLogFromDb();

        if (jsonObject !=null && jsonObject.length() > 0) {
            //继续执行
            _count = _chain.process(jsonObject, null);

            //删除数据
            if (_count != -1) {
                _count = deleteLogFromDb();
            }

            //重置数据库索引
            if (0 < _count && _count < LogBaseConstant.Constant.NUM_LOG_PER_TIME) {
                mDbManager.resetPrimaryKey(LogBaseTableInfo.CommonLog.TABLE_NAME);
            }
        }
        return _count;
    }

    private JSONObject getLogFromDb() {
        String _numStr = String.valueOf(LogBaseConstant.Constant.NUM_LOG_PER_TIME);
        Cursor _cursor = mDbManager.getCommonLogLimitNum(_numStr);
        //游标为空时直接返回
        if (_cursor == null) {
            return null;
        }
        return getJsonObjectFromCursor(_cursor);
    }

    private int deleteLogFromDb() {
        LogBaseLog.d("deleteLogFromDb: " +mStartIndex+"-"+mEndIndex);
        return mDbManager.deleteCommonLog(mStartIndex, mEndIndex);
    }

    /**
     * 根据数据库游标 组装要上传的json数据
     *
     * @param cursor 游标
     * @return 上传的日志Json
     */
    private JSONObject getJsonObjectFromCursor(Cursor cursor) {
        //全部日志对象
        JSONObject _jsonObject = new JSONObject();

        if (cursor != null && cursor.getCount() > 0) {
            //日志id下标
            int _idIndex;
            //日志log下标
            int _logIndex;
            //一条日志
            JSONObject _jsonItem;
            //日志数组
            JSONArray _jsonArray = new JSONArray();

            if (cursor.moveToFirst()) {
                _idIndex = cursor.getColumnIndex(LogBaseTableInfo.CommonLog.Column.ID);
                _logIndex = cursor.getColumnIndex(LogBaseTableInfo.CommonLog.Column.LOG);

                mStartIndex = cursor.getInt(_idIndex);
                mEndIndex = mStartIndex;
                _jsonItem = getLogFromCursorWithLogIndex(cursor, _logIndex);
                if (_jsonItem != null) {
                    _jsonArray.put(_jsonItem);
                }
                while (cursor.moveToNext()) {
                    _jsonItem = getLogFromCursorWithLogIndex(cursor, _logIndex);
                    if (_jsonItem != null) {
                        _jsonArray.put(_jsonItem);
                    }
                    mEndIndex = cursor.getInt(_idIndex);
                }
            }
            if (_jsonArray.length() > 0) {
                try {
                    _jsonObject.put(LogBaseConstant.Constant.LOG_ARRAY, _jsonArray);
                    LogBaseLog.d("getLogFromDb:"+_jsonObject.toString());
                    LogBaseLog.i("getLogFromDb:"+_jsonArray.length());
                } catch (Exception e) {
                    LogBaseLog.w(e);
                }
            }
        }
        return _jsonObject;
    }

    /**
     * 根据数据库游标 得到某一条日志
     *
     * @param cursor   游标
     * @param logIndex 日志所在数据库表的位置
     * @return 数据库返回结果
     */
    private JSONObject getLogFromCursorWithLogIndex(Cursor cursor, int logIndex) {
        JSONObject _jsonObject = null;
        if (cursor != null) {
            try {
                byte[] encryBytes = Base64.decode(cursor.getBlob(logIndex), Base64.DEFAULT);
                byte[] decryptBytes = mEncrypt.decrypt(encryBytes);
                if (decryptBytes != null && decryptBytes.length > 0) {
                    _jsonObject = new JSONObject(new String(decryptBytes, Charset.forName("utf-8")));
                }
            } catch (JSONException e) {
                LogBaseLog.w(e);
            }
        }
        return _jsonObject;
    }
}
