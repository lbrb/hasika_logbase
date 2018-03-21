package com.migu.data.android.logbase;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.migu.data.android.logbase.database.ILogBaseDbManager;
import com.migu.data.android.logbase.database.LogBaseSQLiteDbManager;
import com.migu.data.android.logbase.device.ILogBaseLocalStore;
import com.migu.data.android.logbase.device.LogBasePreferencesLocalStore;
import com.migu.data.android.logbase.encrypt.ILogBaseEncrypt;
import com.migu.data.android.logbase.encrypt.LogBaseAesEncrypt;
import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.util.LogBaseLog;
import com.migu.data.android.logbase.util.LogBaseNetChangeReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hasika on 2017/9/19.
 * 1.线程不安全, 由业务层保证所有日志埋点都在LogBaseThreadPool.execute方法内执行
 * 2.提供设备信息，日志上传等公共方法。
 *
 * 上传日志的三种方式：
 * 1.saveLog 异步接口
 * 日志会存在本地数据库中，每隔periodWithSecond秒统一上传一次，适合对数据不是很敏感的场景，比如数据采集sdk。
 *
 * 2.syncSaveLog 同步接口，适合退出日志。
 * 日志会存在本地数据库中，每隔periodWithSecond秒统一上传一次，适合对数据不是很敏感的场景，比如数据采集sdk。
 *
 * 3. uploadLog,异步接口
 * 日志数据会立即上传，上传失败后会保存在本地，待periodWithSecond秒后再上传。适合计费场景
 */

public class LogBaseContext {

    //插入数据库worker
    private final LogBaseSaveWorker mSaveWorker;
    private final LogBaseUploadLogWorker mUploadLogWorker;

    private LogBaseContext(Builder build) {
        Context mContext = build.mAppContext;
        String mUploadLogUrlStr = build.mUploadLogUrlStr;
        String mSecondUploadUrlStr = build.mSecondUploadUrlStr;
        String mQueryServerInfoUrl = build.mQueryServerInfoUrl;
        String mDbName = build.mDbName;
        String mEncryptKeyStr = build.mEncryptKeyStr;
        int mPeriodWithSecond = build.mPeriodWithSecond;
        boolean mIsDebugMode = build.mIsDebugMode;
        List<ILogBaseInterceptor> mList = build.mList;

        LogBaseLog.setLogLevel(mIsDebugMode);

        ILogBaseDbManager mDbManager = new LogBaseSQLiteDbManager(mContext, mDbName);
        ILogBaseLocalStore mLocalStore = new LogBasePreferencesLocalStore(mContext, mEncryptKeyStr);
        ILogBaseEncrypt mEncrypt = new LogBaseAesEncrypt(mEncryptKeyStr, mLocalStore);

        mSaveWorker = new LogBaseSaveWorker(mDbManager, mEncrypt);
        mUploadLogWorker = new LogBaseUploadLogWorker(mUploadLogUrlStr, mSecondUploadUrlStr, mPeriodWithSecond, mDbManager, mEncrypt, mLocalStore, mList, mSaveWorker);
        //注册网络监听
        registerNetworkChangeListener(mContext);

        //查询服务器信息，比如时间
        if (!TextUtils.isEmpty(mQueryServerInfoUrl)) {
            LogBaseQueryServiceInfoWorker mQueryServiceInfoWorker = new LogBaseQueryServiceInfoWorker(mQueryServerInfoUrl, mLocalStore);

            mQueryServiceInfoWorker.query();
        }

        //每三分钟上传一次日志
        mUploadLogWorker.uploadLog();
    }

    private void registerNetworkChangeListener(Context context) {
        LogBaseNetChangeReceiver netReceiver = new LogBaseNetChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(netReceiver, filter);
    }

    /**
     * 异步保存日志
     * 所有的日志逻辑都必须要指定线程成执行，否则直接报错。执行方法LogBaseThreadPool.execute();
     *
     * @param jsonObject 待上传数据
     */
    private void saveLog(JSONObject jsonObject) {
        mSaveWorker.saveLog(jsonObject);
    }

    /**
     * 异步保存日志
     *
     * @param map 待上传数据
     */
    public void saveLog(Map<String, String> map) {
        if (map == null || map.size() < 1) {
            return;
        }
        JSONObject _jsonObject = new JSONObject(map);
        saveLog(_jsonObject);
    }

    /**
     * 同步保存方法
     *
     * @param map 日志数据
     */
    public void syncSaveLog(Map<String, String> map) {
        if (map == null || map.size() < 1) {
            return;
        }
        JSONObject _jsonObject = new JSONObject(map);
        syncSaveLog(_jsonObject);
    }

    /**
     * 同步保存方法
     * @param jsonObject 日志数据
     */
    private void syncSaveLog(JSONObject jsonObject) {
        mSaveWorker.syncSaveLog(jsonObject);
    }

    /**
     * 立即上传日志
     * @param jsonObject 日志数据
     */
    public void uploadLog(JSONObject jsonObject) {
        mUploadLogWorker.uploadLogNow(jsonObject);
    }

    /**
     * 立即上传日志
     * @param map 日志数据
     */
    public void uploadLog(Map<String, String> map) {
        if (map == null || map.size() < 1) {
            return;
        }
        JSONObject _jsonObject = new JSONObject(map);
        uploadLog(_jsonObject);
    }



    public static class Builder {
        private String mUploadLogUrlStr;
        private String mSecondUploadUrlStr;
        private String mQueryServerInfoUrl;
        private String mDbName = "miguDataBase.db";
        private String mEncryptKeyStr = "miguDataBase";
        private int mPeriodWithSecond = 60;
        private final Context mAppContext;
        private boolean mIsDebugMode = false;
        private final List<ILogBaseInterceptor> mList = new ArrayList<>();

        public Builder(Context context) {
            mAppContext = context.getApplicationContext();
        }

        public Builder uploadLogUrl(String url) {
            mUploadLogUrlStr = url;
            return this;
        }

        public Builder secondUploadLogUrl(String url) {
            mSecondUploadUrlStr = url;
            return this;
        }

        public Builder queryServerInfoUrl(String url) {
            this.mQueryServerInfoUrl = url;
            return this;
        }

        public Builder dbName(String name) {
            this.mDbName = name;
            return this;
        }

        public Builder encryptKey(String key) {
            this.mEncryptKeyStr = key;
            return this;
        }

        public Builder periodWithSecond(int periodWithSecond) {
            this.mPeriodWithSecond = periodWithSecond;
            return this;
        }

        public Builder debug(boolean b) {
            this.mIsDebugMode = b;
            return this;
        }

        public Builder addInterceptor(ILogBaseInterceptor interceptor) {
            mList.add(interceptor);
            return this;
        }

        public LogBaseContext build() {
            return new LogBaseContext(this);
        }

    }
}
