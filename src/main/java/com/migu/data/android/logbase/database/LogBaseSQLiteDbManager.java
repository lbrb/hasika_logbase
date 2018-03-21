package com.migu.data.android.logbase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.migu.data.android.logbase.util.LogBaseLog;

/**
 * Created by hasika on 2017/5/22.
 * <p>
 * 数据库管理类，提供增删查功能
 */

public class LogBaseSQLiteDbManager implements ILogBaseDbManager {

    private final SQLiteDatabase mDb;

    public LogBaseSQLiteDbManager(Context context, String dbName) {
        LogBaseSqliteOpenHelper helper = new LogBaseSqliteOpenHelper(context, dbName);
        this.mDb = helper.getWritableDatabase();
    }

    /**
     * 查询数据库数据
     *
     * @param limitNum 查询的数据条数
     * @return 数据库游标
     */
    public Cursor getCommonLogLimitNum(String limitNum) {
        String sql = String.format("SELECT * FROM %s LIMIT %s", LogBaseTableInfo.CommonLog.TABLE_NAME, limitNum);

        return mDb.rawQuery(sql, null);
    }

    /**
     * 删除数据库记录
     *
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return 删除的数据库记录数
     */
    public int deleteCommonLog(int startIndex, int endIndex) {
        if (startIndex == 0 && endIndex == 0) {
            return 0;
        }

        int count = 0;
        try {
            count = mDb.delete(LogBaseTableInfo.CommonLog.TABLE_NAME, LogBaseTableInfo.CommonLog.Column.ID + " BETWEEN ? AND ?", new String[]{String.valueOf(startIndex), String.valueOf(endIndex)});
            LogBaseLog.i("deleteCommonLog, delete logNum: " + count);
        } catch (Exception e) {
            LogBaseLog.w(e);
        }

        return count;

    }

    /**
     * 插入数据
     *
     * @param bytes 插入的字节数据
     * @return 插入数据的index
     */
    public long insertCommonLog(byte[] bytes) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(LogBaseTableInfo.CommonLog.Column.LOG, bytes);
        return mDb.insert(LogBaseTableInfo.CommonLog.TABLE_NAME, null, contentValues);
    }

    /**
     * 数据上传完毕，删除数据库数据后，将数据库index复位0
     *
     * @param tableName 数据库名
     */
    public void resetPrimaryKey(String tableName) {
        int i = mDb.delete("sqlite_sequence", "name = ?", new String[]{tableName});

        LogBaseLog.d("resetPrimaryKey, " + tableName + "," + i);
    }
}
