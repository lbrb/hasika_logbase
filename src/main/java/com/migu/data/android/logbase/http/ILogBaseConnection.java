package com.migu.data.android.logbase.http;

import org.json.JSONObject;

/**
 * Created by hasika on 2017/10/6.
 * <p>
 * 上传日志
 */

public interface ILogBaseConnection {
    /**
     * 发起请求
     *
     * @param url 请求地址
     * @return 返回值
     */
    JSONObject request(String url);

    /**
     * 发起请求
     *
     * @param bytes 字节数组
     * @param url   请求地址
     * @return 返回值
     */
    JSONObject request(byte[] bytes, String url);
}
