package com.migu.data.android.logbase;

import com.migu.data.android.logbase.database.ILogBaseDbManager;
import com.migu.data.android.logbase.device.ILogBaseLocalStore;
import com.migu.data.android.logbase.encrypt.ILogBaseEncrypt;
import com.migu.data.android.logbase.thread.LogBaseThreadPool;
import com.migu.data.android.logbase.upload_log.ILogBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseContinueDoInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseDealLogFromDataBaseInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseFixInvokeTimeInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseHttpInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseSaveLogWhenErrorInterceptor;
import com.migu.data.android.logbase.upload_log.LogBaseUploadLogInterceptorChain;
import com.migu.data.android.logbase.util.LogBaseLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasika on 2017/9/20.
 * 上传数据
 */

class LogBaseUploadLogWorker {
    private final String mUrl;
    private final String mSecondUrl;
    private final int mPeriodWithSecond;
    private final ILogBaseDbManager mDbManager;
    private final ILogBaseEncrypt mEncrypt;
    private final ILogBaseLocalStore mSharePreferences;
    private final List<ILogBaseInterceptor> mInsertList;
    private final LogBaseSaveWorker mSaveWroker;

    private LogBaseUploadLogInterceptorChain mChain;

    private LogBaseUploadLogInterceptorChain mUploadNowChain;


    /**
     * 日志上传worker
     *
     * @param url              上传地址
     * @param periodWithSecond 上传日志的时间间隔
     * @param dbManager        数据库句柄
     * @param encrypt          加解密句柄
     * @param sharePreferences 本地缓存句柄
     */
    LogBaseUploadLogWorker(String url, String secondUrl, int periodWithSecond, ILogBaseDbManager dbManager, ILogBaseEncrypt encrypt, ILogBaseLocalStore sharePreferences, List<ILogBaseInterceptor> interceptors, LogBaseSaveWorker saveWorker) {
        this.mUrl = url;
        this.mSecondUrl = secondUrl;
        this.mPeriodWithSecond = periodWithSecond;
        this.mDbManager = dbManager;
        this.mEncrypt = encrypt;
        this.mSharePreferences = sharePreferences;
        this.mInsertList = interceptors;
        this.mSaveWroker = saveWorker;
    }

    /**
     * 开始上传日志工作，每x秒上传一次
     */
    public void uploadLog() {
        LogBaseThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                realUploadLog();
            }
        }, mPeriodWithSecond);
    }

    /**
     * 上传日志
     *
     */
    private void realUploadLog() {
        LogBaseLog.d("realUploadLog");
        uploadLogWithInterceptorChain();
    }

    /**
     * 使用链表上传日志
     * 流程和OSI类似，从第一个跑到最后一个，在从最后一个跑到第一个
     *
     */
    private void uploadLogWithInterceptorChain() {

        if (mChain == null) {
            List<ILogBaseInterceptor> interceptors = new ArrayList<>();
            //因为日志数据可能比较多，上传成功后，会判断是否需要继续上传日志
            //判断如果需要则上传，不需要退出方法
            interceptors.add(new LogBaseContinueDoInterceptor());
            //从数据库中查询数据
            //网络传输完成后，如果数据已经上传完成，则恢复数据索引为0
            interceptors.add(new LogBaseDealLogFromDataBaseInterceptor(mEncrypt, mDbManager));
            //根据服务端时间，修复客户端时间
            interceptors.add(new LogBaseFixInvokeTimeInterceptor(mSharePreferences));

            //添加外部自定义interceptors
            if (mInsertList != null && mInsertList.size() > 0) {
                interceptors.addAll(mInsertList);
            }

            //发起网络请求
            interceptors.add(new LogBaseHttpInterceptor(mUrl, mSecondUrl));

            mChain = new LogBaseUploadLogInterceptorChain(interceptors);
        }


        mChain.process(null, null);

    }

    /**
     * 直接上传数据，不通过查询数据库的方式，适用计费sdk，适用场景较为特殊，存在时间短。
     *
     * @param jsonObject 日志数据
     */
    public void uploadLogNow(final JSONObject jsonObject){
        LogBaseThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                uploadLogNowInternal(jsonObject);
            }
        });
    }

    private void uploadLogNowInternal(JSONObject jsonObject) {
        if (mUploadNowChain == null) {
            List<ILogBaseInterceptor> interceptors = new ArrayList<>();

            //上传失败，保存本地
            interceptors.add(new LogBaseSaveLogWhenErrorInterceptor(mSaveWroker));

            //根据服务端时间，修复客户端时间
            interceptors.add(new LogBaseFixInvokeTimeInterceptor(mSharePreferences));

            //添加外部自定义interceptors
            if (mInsertList != null && mInsertList.size() > 0) {
                interceptors.addAll(mInsertList);
            }

            //发起网络请求
            interceptors.add(new LogBaseHttpInterceptor(mUrl, mSecondUrl));

            mUploadNowChain = new LogBaseUploadLogInterceptorChain(interceptors);
        }

        mUploadNowChain.process(jsonObject, null);
    }
}
