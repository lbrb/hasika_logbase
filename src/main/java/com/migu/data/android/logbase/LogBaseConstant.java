package com.migu.data.android.logbase;

/**
 * Created by hasika on 2017/9/19.
 * 常量类
 */

public final class LogBaseConstant {

    public final class Constant {
        //日志数组的key
        public static final String LOG_ARRAY = "log";
        //日志的客户端时间
        public static final String INVOKE_TIME = "invokeTime";
        //开始时间
        public static final String START_TIME = "startTime";
        //结束时间
        public static final String END_TIME = "endTime";
        //一次上传100条
        public static final int NUM_LOG_PER_TIME = 100;
        //线程名
        public static final String THREAD_NAME = "miguGameLogBaseThred";
        //系统名
        public static final String PHONE_SYSTEM = "Android";
    }

    public final class Operator {
        public static final String CMCC = "1";
        public static final String CUCC = "2";
        public static final String CTCC = "3";
        public static final String Other = "0";
    }

    public final class ApnType {
        public static final String NONE = "NONE";
        public static final String WIFI = "WIFI";
        public static final String CMNET = "CMNET";
        public static final String CMWAP = "CMWAP";
        public static final String UNINET = "UNNET";
        public static final String UNIWAP = "UNWAP";
        public static final String GNET = "3GNET";
        public static final String GWAP = "3GWAP";
        public static final String CTNET = "CTNET";
        public static final String CTWAP = "CTWAP";
        public static final String OTHER = "OTHER";
    }

    public final class NetworkType {
        public static final String Network_UNKNOWN = "0";
        public static final String Network_2G = "1";
        public static final String Network_3G = "2";
        public static final String Network_4G = "3";
        public static final String Network_WIFI = "4";
        public static final String Network_NONE = "5";
    }

    public final class NetworkSubType {
        public static final int NETWORK_TYPE_IDEN = 11;
        public static final int NETWORK_TYPE_EVDO_B = 12;
        public static final int NETWORK_TYPE_LTE = 13;
        public static final int NETWORK_TYPE_EHRPD = 14;
        public static final int NETWORK_TYPE_HSPAP = 15;
        public static final int NETWORK_TYPE_TD_SCDMA = 17;
    }
}
