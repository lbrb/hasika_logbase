package com.migu.data.android.logbase.upload_log;

import org.json.JSONObject;

/**
 * Created by hasika on 2017/9/21.
 * 拦截器接口
 */

public interface ILogBaseInterceptor {
    /**
     *
     * @param chain 拦截器链表
     * @return 上传的日志个数；-1表示失败，其他表示日志个数
     */
    long intercept(IChain chain);

    /**
     * 拦截器链表
     */
    interface IChain {
        /**
         * 执行下一个拦截器
         *
         * @param jsonObject 待上传的数据
         * @param bytes 待上传的数据
         * @return 正确执行的数量
         */
        long process(JSONObject jsonObject, byte[] bytes);
    }

}
