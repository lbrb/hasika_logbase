package com.migu.data.android.logbase.encrypt;

import android.text.TextUtils;

import com.migu.data.android.logbase.device.ILogBaseLocalStore;
import com.migu.data.android.logbase.device.LogBasePreferencesLocalStore;
import com.migu.data.android.logbase.util.LogBaseLog;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hasika on 2017/5/9.
 * AES 加解密
 */

public class LogBaseAesEncrypt implements ILogBaseEncrypt {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    //外层传进来的密钥
    private final String mEncryptKey;
    //处理后的密钥，如果没有值就按算法产生一个并保存在本地；如果有值就直接使用；
    private String mRawKey;

    private final ILogBaseLocalStore mLocalStore;

    public LogBaseAesEncrypt(String encryptKey, ILogBaseLocalStore sharePreferences) {
        this.mEncryptKey = encryptKey;
        this.mLocalStore = sharePreferences;
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getRawKey(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            LogBaseLog.w(e);
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getRawKey(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            LogBaseLog.w(e);
        }
        return null;
    }

    /**
     * 获取密钥
     * @return 密钥
     * @throws Exception 异常信息
     */
    private synchronized byte[] getRawKey() throws Exception {

        if (TextUtils.isEmpty(this.mRawKey)) {
            this.mRawKey = mLocalStore.getString(LogBasePreferencesLocalStore.SHARED_PREFERENCES_KEY+mEncryptKey);

            if (TextUtils.isEmpty(this.mRawKey)) {
                this.mRawKey = getEncryptKey();
                mLocalStore.putString(LogBasePreferencesLocalStore.SHARED_PREFERENCES_KEY+mEncryptKey, this.mRawKey);
            }

        }
        return getMD5(this.mRawKey.getBytes());
    }

    /**
     * 根据传进来的参数，生成密钥
     * @return 密钥
     */
    private String getEncryptKey() {
        String _encryptKey = "";

        StringBuilder _encryptKeyStrBuild = new StringBuilder();
        if (!TextUtils.isEmpty(mEncryptKey)) {
            char c;
            for (int i = 0; i < mEncryptKey.length(); i++) {
                c = mEncryptKey.charAt(i);
                if (Character.isDigit(c)) {
                    if (Integer.parseInt(Character.toString(c)) == 0)
                        _encryptKeyStrBuild.append(0);
                    else
                        _encryptKeyStrBuild.append(10 - Integer.parseInt(Character.toString(c)));
                } else {
                    _encryptKeyStrBuild.append(c);
                }
            }
            _encryptKey = _encryptKeyStrBuild.toString();
            _encryptKey += (new StringBuilder(_encryptKey)).reverse();
        }

        return _encryptKey;
    }

    private byte[] getMD5(byte[] bytes) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(bytes);
        return messageDigest.digest();
    }
}
