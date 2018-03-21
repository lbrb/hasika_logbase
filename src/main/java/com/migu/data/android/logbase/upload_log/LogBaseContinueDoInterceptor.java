package com.migu.data.android.logbase.upload_log;

import com.migu.data.android.logbase.LogBaseConstant;
import com.migu.data.android.logbase.util.LogBaseLog;

/**
 * Created by hasika on 2017/9/21.
 *
 * 循环上传日志拦截器
 * 如果上次上传的日志个数等于NUM_LOG_PER_TIME，则认为数据库中还有数据，继续上传
 * 如果上次上传的日志个数不等于NUM_LOG_PER_TIME，则认为数据库中没有数据了，或者本次上传失败，等待下个机会再上传
 */

public class LogBaseContinueDoInterceptor implements ILogBaseInterceptor {
    @Override
    public long intercept(IChain chain) {
        long _count = -1;

        for (int i = 0; i < 5; i++) {
            _count = chain.process(null,null);
            LogBaseLog.d("LogBaseContinueDoInterceptor: count:"+_count);
            //等于每次最大条数，说明数据库中还有数据，继续执行
            if (_count != LogBaseConstant.Constant.NUM_LOG_PER_TIME) {
                break;
            }
        }

        return _count;
    }
}
