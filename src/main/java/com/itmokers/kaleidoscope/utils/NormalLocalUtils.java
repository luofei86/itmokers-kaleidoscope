package com.itmokers.kaleidoscope.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NormalLocalUtils {
    public static void main(String[] args) {
        System.out.println(YMD_HMS_FORMAT);
    }

    public static final String YMD_HMS_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + " " + DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.getPattern();

    public static boolean isNumberClass(Class<?> clazz) {
        return clazz == byte.class || clazz == Byte.class || clazz == short.class || clazz == Short.class || clazz == int.class
                || clazz == Integer.class || clazz == float.class || clazz == Float.class || clazz == double.class || clazz == Double.class
                || clazz == long.class || clazz == Long.class;
    }

    public static boolean isDateClass(Class<?> attributeClass) {
        return (attributeClass.equals(Date.class) || attributeClass.equals(java.sql.Date.class));
    }

    public static <M> List<List<M>> splitList(List<M> values, int parallelSize) {
        List<List<M>> result = new ArrayList<>();
        if (values.size() <= parallelSize) {
            result.add(values);
            return result;
        }
        int listSize;
        if (values.size() % parallelSize == 0) {
            listSize = values.size() / parallelSize;
        } else {
            listSize = (values.size() / parallelSize) + 1;
        }

        for (int i = 0; i < listSize; i++) {
            int fromIndex = i * parallelSize;
            int toIndex = (i + 1) * parallelSize;
            if (toIndex > values.size()) {
                toIndex = values.size();
            }
            result.add(values.subList(fromIndex, toIndex));
        }
        return result;
    }
}
