package com.migu.data.android.logbase.database;

/**
 * Created by hasika on 2017/9/21.
 * 数据库表结构
 */

public class LogBaseTableInfo {
    //create table if not exists common_log(_id INTEGER primary key autoincrement, _log TEXT);
    public static class CommonLog {
        public static final String TABLE_NAME = "common_log";

        public static class Column {
            public final static String ID = "_id";
            public final static String LOG = "_log";
        }

        public static class ColumnDesc {
            public final static String ID = "INTEGER primary key autoincrement";
            public final static String LOG = "TEXT";
        }
    }
}
