package com.migu.data.android.logbase.database;

import android.database.Cursor;

/**
 * Created by hasika on 2017/10/6.
 *
 * 数据库管理类，提供增删查功能
 */

public interface ILogBaseDbManager {
    /**
     * 查询数据库数据
     *
     * @param limitNum 查询的数据条数
     * @return 数据库游标
     */
    Cursor getCommonLogLimitNum(String limitNum);

    /**
     * 删除数据库记录
     *
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return 删除的数据库记录数
     */
    int deleteCommonLog(int startIndex, int endIndex);

    /**
     * 插入数据
     *
     * @param bytes 插入的字节数据
     * @return 插入数据的index
     */
    long insertCommonLog(byte[] bytes);

    /**
     * 数据上传完毕，删除数据库数据后，将数据库index复位0
     *
     * @param tableName 数据库名
     */
    void resetPrimaryKey(String tableName);
}
