//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.itmokers.kaleidoscope.enums;


import com.itmokers.kaleidoscope.domain.DbFields;
import com.itmokers.kaleidoscope.utils.NormalLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public enum EnumSqlConditionalSymbol {
    EQ {
        @Override
        public String getSymbol() {
            return " = ";
        }
    }, NOT_EQ {
        @Override
        public String getSymbol() {
            return " != ";
        }
    }, IN {
        @Override
        public String getSymbol() {
            return " IN ";
        }
    }, NOT_IN {
        @Override
        public String getSymbol() {
            return " NOT IN ";
        }
    }, GREATER {
        @Override
        public String getSymbol() {
            return " &gt; ";
        }
    }, GREATER_EQ {
        @Override
        public String getSymbol() {
            return " &gt;= ";
        }
    }, LESS {
        @Override
        public String getSymbol() {
            return " &lt; ";
        }
    }, LESS_EQ {
        @Override
        public String getSymbol() {
            return " &lt;= ";
        }
    };

    public abstract String getSymbol();

    /**
     * TODO
     *
     * @param clazz     clazz
     * @param data      data
     * @param column    column
     * @param attribute attribute
     * @param symbol    symbol
     * @param <G>       g
     * @return inner sql
     */
    private static final <G> String buildInnerSql(Class<G> clazz, Object data, String column, String attribute, String symbol) {
        if (StringUtils.isEmpty(column) || clazz == null || data == null || StringUtils.isEmpty(attribute)) {
            return "";
        }
        Class<?> attributeClass = BeanUtils.findPropertyType(attribute, clazz);
        StringBuilder sb = new StringBuilder();
        if (data instanceof Collection<?>) {
            Collection<?> dataList = (Collection<?>) data;
            if (CollectionUtils.isEmpty(dataList)) {
                return "";
            }
            if (NormalLocalUtils.isDateClass(attributeClass)) {
                for (Object innerData : dataList) {
                    if (innerData == null) {
                        continue;
                    }
                    String appendValue = DateFormatUtils.format((Date) innerData, NormalLocalUtils.YMD_HMS_FORMAT);
                    if (sb.length() == 0) {
                        sb.append(" AND ").append(column).append(symbol).append("(");
                        sb.append(" '").append(appendValue).append("'");
                    } else {
                        sb.append(", '").append(appendValue).append("'");
                    }
                }
            } else if (NormalLocalUtils.isNumberClass(attributeClass)) {
                for (Object innerData : dataList) {
                    if (innerData == null) {
                        continue;
                    }
                    if (sb.length() == 0) {
                        sb.append(" AND ").append(column).append(symbol).append("(");
                        sb.append(" ").append(innerData);
                    } else {
                        sb.append(", ").append(innerData);
                    }
                }
            } else {
                for (Object innerData : dataList) {
                    if (innerData == null) {
                        continue;
                    }
                    if (sb.length() == 0) {
                        sb.append(" AND ").append(column).append(symbol).append("(");
                        sb.append(" '").append(innerData).append("'");
                    } else {
                        sb.append(", '").append(innerData).append("'");
                    }
                }
            }
        } else {
            sb.append(" AND ").append(column).append(symbol).append("(");
            if (NormalLocalUtils.isDateClass(attributeClass)) {
                sb.append(" '").append(DateFormatUtils.format((Date) data, NormalLocalUtils.YMD_HMS_FORMAT)).append("'");
            } else if (NormalLocalUtils.isNumberClass(attributeClass)) {
                sb.append(data);
            } else {
                sb.append(" '").append(data.toString()).append("'");
            }
        }
        if (sb.length() == 0) {
            return "";
        }
        sb.append(")");
        return sb.toString();
    }

    public <G> String toSql(Class<G> clazz, Object data, String column) {
        return toSql(clazz, data, column, DbFields.columnToAttributeName(column));
    }

    public <G> String toSql(Class<G> clazz, Object data, String column, String attribute) {
        if (data == null || StringUtils.isEmpty(column)) {
            return "";
        }
        Class<?> attributeClass = BeanUtils.findPropertyType(attribute, clazz);
        if (NormalLocalUtils.isNumberClass(attributeClass)) {
            return " AND `" + column + "` " + getSymbol() + data;
        }
        if (NormalLocalUtils.isDateClass(attributeClass)) {
            return " AND `" + column + "` " + getSymbol() + "'" + DateFormatUtils.format((Date) data, NormalLocalUtils.YMD_HMS_FORMAT) + "'";
        }
        return " AND `" + column + "` " + getSymbol() + "\"" + data.toString() + "\"";
    }


}
