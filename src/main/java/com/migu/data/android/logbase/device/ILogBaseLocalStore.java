package com.migu.data.android.logbase.device;

/**
 * Created by hasika on 2017/10/6.
 * 本地保存接口
 */

public interface ILogBaseLocalStore {
    String getString(String k);
    void putString(String k, String v);
    long getLong(String k);
    void putLong(String k, long v);

}
