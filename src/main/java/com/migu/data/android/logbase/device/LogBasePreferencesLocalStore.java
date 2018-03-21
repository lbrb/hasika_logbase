package com.migu.data.android.logbase.device;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hasika on 2017/9/20.
 * 信息存储
 */

public class LogBasePreferencesLocalStore implements ILogBaseLocalStore {
    public static final String SHARED_PREFERENCES_KEY = "logBaseRawKeyStr";
    public static final String TIME_MILLIS_DIFF = "timeMilllsDiff";

    private final SharedPreferences mSharePreferences;
    private final SharedPreferences.Editor mEditor;

    public LogBasePreferencesLocalStore(Context context, String encryptKeyStr) {
        mSharePreferences = context.getSharedPreferences(encryptKeyStr, Context.MODE_PRIVATE);
        mEditor = mSharePreferences.edit();
        mEditor.apply();
    }

    public String getString(String k) {
        return this.mSharePreferences.getString(k, "");
    }

    public void putString(String k, String v) {
        mEditor.putString(k, v);
        mEditor.apply();
    }

    public long getLong(String k) {
        return this.mSharePreferences.getLong(k, 0);
    }

    public void putLong(String k, long v) {
        mEditor.putLong(k, v);
        mEditor.apply();
    }
}
