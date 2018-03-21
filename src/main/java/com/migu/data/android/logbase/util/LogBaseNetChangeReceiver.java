package com.migu.data.android.logbase.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.migu.data.android.logbase.LogBaseDeviceInfo;

/**
 * Created by hasika on 2017/9/19.
 * 网络变化监听模块
 */

public class LogBaseNetChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isAvailable()) {

                        LogBaseDeviceInfo.NETWORK_TYPE = networkInfo.getType();
                        LogBaseDeviceInfo.NETWORK_SUBTYPE = networkInfo.getSubtype();
                        LogBaseDeviceInfo.NETWORK_EXTRA_INFO = networkInfo.getExtraInfo();
                        LogBaseLog.d("activeNetworkInfo, extraInfo:" + LogBaseDeviceInfo.NETWORK_EXTRA_INFO+", subType:"+ LogBaseDeviceInfo.NETWORK_SUBTYPE+", type:"+ LogBaseDeviceInfo.NETWORK_TYPE);
                    } else {
                        LogBaseDeviceInfo.NETWORK_TYPE = -1;
                        LogBaseDeviceInfo.NETWORK_SUBTYPE = -1;
                        LogBaseDeviceInfo.NETWORK_EXTRA_INFO = "";
                        LogBaseLog.d("no network");
                    }
                }
            }catch (Exception e) {
                LogBaseLog.w(e);
            }
        }
    }
}
