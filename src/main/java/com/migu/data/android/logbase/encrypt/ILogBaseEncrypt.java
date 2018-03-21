package com.migu.data.android.logbase.encrypt;

/**
 * Created by hasika on 2017/5/16.
 * 加解密接口
 */

public interface ILogBaseEncrypt {
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);
}
