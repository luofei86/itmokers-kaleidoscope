//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.itmokers.kaleidoscope.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class DbFields {
    public static final String ID_COLUMN = "id";
    public static final int DEL_FLAG_OK = 0;
    public static final int MAX_QUERY_LIST_SIZE = 1000;

    public static final int THREADS = Runtime.getRuntime().availableProcessors();

    public static final String DEL_FLAG_COLUMN = "del_flag";
    public static final String DEL_FLAG_ATTRIBUTE = "delFlag";
    public static final String DEL_FLAG_NAME = "delFlag";
    public static final String STATUS_COLUMN = "del_flag";
    public static final String UPDATE_TIME_COLUMN = "update_time";
    public static final String CREATE_TIME_COLUMN = "create_time";
    public static final String UPDATE_TIME_NAME = "updateTime";
    public static final String CREATE_TIME_NAME = "createTime";


    public static final String SIZE_SQL_PARAMETER = "size";
    public static final String START_SQL_PARAMETER = "start";
    public static final String START_ID_SQL_PARAMETER = "startId";
    public static final String END_ID_SQL_PARAMETER = "endId";
    public static final String ID_SQL_PARAMETER = "id";
    public static final String IDS_SQL_PARAMETER = "ids";
    public static final String TABLE_NAME_PARAMETER = "tableName";
    public static final String ST_SQL_PARAMETER = "st";
    public static final String ET_SQL_PARAMETER = "et";
    public static final String ORDER_COLUMN_SORT_SQL_PARAMETER = "orderColumnSort";
    public static final String ORDER_COLUMN_SQL_PARAMETER = "orderColumn";
    public static final String EXPRESSIONS_SQL_PARAMETER = "expressions";

    public static final int SELECT_ALL_INT_COLUMN_ALL_VALUE = 9999;
    public static final String SELECT_ALL_INT_COLUMN_ALL_VALUE_DESC = String.valueOf(SELECT_ALL_INT_COLUMN_ALL_VALUE);

    public static final String SELECT_ALL_STRING_COLUMN_ALL_VALUE = "ALL";
    public static final String SELECT_ALL_STRING_COLUMN_ALL_VALUE_DESC = "全部";

    public static final String DEFAULT_NORMAL_SPLIT_CHAR = ",";


    public static String columnToAttributeName(String column) {
        String[] columns = StringUtils.split(column, "_");
        String result = "";
        for (int i = 0; i < columns.length; i++) {
            if (i == 0) {
                result += columns[i];
            } else if (columns[i].length() > 1) {
                result += columns[i].substring(0, 1).toUpperCase() + columns[i].substring(1);
            } else if (columns[i].length() == 1) {
                result += columns[i].toUpperCase();
            }
        }
        return result;
    }
}
