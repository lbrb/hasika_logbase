package com.migu.data.android.logbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.migu.data.android.logbase.util.LogBaseLog;

/**
 * Created by hasika on 2017/5/22.
 * 数据库访问
 */

class LogBaseSqliteOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    LogBaseSqliteOpenHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            createCommonLogTable(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogBaseLog.w(e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createCommonLogTable(SQLiteDatabase db) {
        try {
            //create table if not exists common_log(_id INTEGER primary key autoincrement, _log TEXT);
            String sql = String.format("create table if not exists %s(%s %s, %s %s);", LogBaseTableInfo.CommonLog.TABLE_NAME, LogBaseTableInfo.CommonLog.Column.ID, LogBaseTableInfo.CommonLog.ColumnDesc.ID, LogBaseTableInfo.CommonLog.Column.LOG, LogBaseTableInfo.CommonLog.ColumnDesc.LOG);
            LogBaseLog.d(sql);
            db.execSQL(sql);
        } catch (Exception e) {
            LogBaseLog.w(e);
        }
    }
}
