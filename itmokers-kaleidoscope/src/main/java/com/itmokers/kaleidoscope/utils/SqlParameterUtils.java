//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.itmokers.kaleidoscope.utils;

import com.itmokers.kaleidoscope.domain.DbFields;
import com.itmokers.kaleidoscope.exception.BizException;
import com.itmokers.kaleidoscope.enums.EnumSqlOrderType;
import com.itmokers.kaleidoscope.model.DbFieldConstraint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author luofei
 * Generate 2020/1/12
 */
@Slf4j
public class SqlParameterUtils {


    public static Map<String, Object> introspect(Object obj, boolean ignoreZero, List<String> noIgnoreZeroField) {
        Map<String, Object> result = new HashMap<>();
        if (obj == null) {
            return result;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getPropertyType() == Class.class) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    handlerBeanReadMethod(pd, reader, result, obj, ignoreZero, noIgnoreZeroField);
                }
            }
        } catch (Exception e) {
            log.error("introspect data:{} failed. Error msg:{}. ", obj, e);
        }
        return result;
    }

    private static void handlerBeanReadMethod(PropertyDescriptor pd, Method reader, Map<String, Object> result, Object obj, boolean ignoreZero, List<String> noIgnoreZeroFieldList) throws InvocationTargetException, IllegalAccessException {
        Object putObject = reader.invoke(obj);
        if (putObject == null) {
            return;
        }
        if (putObject instanceof Date) {
            putObject = DateFormatUtils.format((Date) putObject, NormalLocalUtils.YMD_HMS_FORMAT);
        } else if (putObject instanceof String) {
            if (((String) putObject).isEmpty()) {
                return;
            }
        } else if (ignoreZero && !BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(), DbFieldConstraint.ZERO_ENABLE.class)) {
            if (!handleWhenIgnoreZero(putObject, pd, noIgnoreZeroFieldList)) {
                return;
            }
        }
        if (StringUtils.equals(DbFields.ID_COLUMN, pd.getName())) {
            if (!needBuildId(putObject)) {
                return;
            }
        }
        if (!needBuildUnsigned(pd, putObject, obj)) {
            return;
        }
        // @SEL_ALL_KEY
        // 对于arch字段，如果值为9999，代表查询全部，不用指定查询条件即条件设空
        // check int attribute all value and String
        // attribute all value
        if (BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(), DbFieldConstraint.SELECT_ALL_KEY.class)) {
            if (StringUtils.equalsIgnoreCase(DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE_DESC, putObject.toString()) || StringUtils.equalsIgnoreCase(DbFields.SELECT_ALL_STRING_COLUMN_ALL_VALUE, putObject.toString())) {
                return;
            }
        }
        result.put(pd.getName(), putObject);
    }

    private static boolean handleWhenIgnoreZero(Object putObject, PropertyDescriptor pd, List<String> noIgnoreZeroField) {
        if (noIgnoreZeroField == null || !noIgnoreZeroField.contains(pd.getName())) {
            if (!StringUtils.equals(DbFields.DEL_FLAG_COLUMN, pd.getName())) {
                // status默认0 如果需要查状态为0的数据时，不要过滤，所以此处需要做一点处理 较为丑陋
                if (isZero(putObject)) {
                    return false;
                }
            } else {
                // status全部时，由于数据库中无此状态，所以无需加入到查询条件中，所以此处需要做一点处理
                // 较为丑陋
                int status = Integer.parseInt(String.valueOf(putObject));
                if (status == DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE) {
                    return false;
                }
            }
        }
        // 一些可以为0的标签，如果其值为9999，则略过
        if (noIgnoreZeroField != null && noIgnoreZeroField.contains(pd.getName())) {
            int value = Integer.parseInt(String.valueOf(putObject));
            return value != DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE;
        }
        return true;
    }

    private static boolean needBuildUnsigned(PropertyDescriptor pd, Object putObject, Object obj) {
        if (BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(), DbFieldConstraint.UNSIGNED_INT.class)) {
            int value = Integer.parseInt(String.valueOf(putObject));
            return value >= 0;
        } else if (BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(), DbFieldConstraint.UNSIGNED_LONG.class)) {
            long value = Long.parseLong(String.valueOf(putObject));
            return value >= 0;
        }
        return true;
    }

    private static boolean needBuildId(Object putObject) {
        // 对于id字段，如果不大于0，不进查询条件
        if (putObject instanceof Integer) {
            int value = Integer.parseInt(String.valueOf(putObject));
            return value > 0;
        } else if (putObject instanceof Long) {
            long value = Long.parseLong(String.valueOf(putObject));
            return value > 0;
        } else if (putObject instanceof String) {
            return !((String) putObject).isEmpty();
        }
        return true;
    }

    public static void initTableNameToMap(Map<String, Object> para, String tableSuffix) {
        if (para != null && StringUtils.isNotBlank(tableSuffix)) {
            para.put(DbFields.TABLE_NAME_PARAMETER, tableSuffix);
        }
    }

    /**
     * if will ignore the int value is zero if the dose not have @ZERO_ENABLE
     * annotation
     */
    public static Map<String, Object> buildDomainForQuery(Object g) {
        return introspect(g, true, buildNoIgnoreZeroFieldList(g));
    }


    private static void initPutTableSuffix(Map<String, Object> result, String tableSuffix) {
        if (!StringUtils.isEmpty(tableSuffix) && !StringUtils.startsWith(tableSuffix, "_")) {
            tableSuffix = "_" + tableSuffix;
        }
        result.put("tableSuffix", tableSuffix);
    }

    public static void initTableSuffix(Map<String, Object> result, String tableSuffix) {
        initPutTableSuffix(result, tableSuffix);
    }

    private static boolean isZero(Object object) {
        return isZero(object, int.class, Integer.class, float.class, Float.class, double.class, Double.class, long.class, Long.class, short.class, Short.class);
    }

    private static boolean isZero(Object object, Class<?>... classList) {
        if (object == null) {
            return false;
        }
        Class<?> objectClass = object.getClass();
        for (Class<?> clazz : classList) {
            if (clazz == objectClass) {
                return ((Number) object).equals(clazz.cast(0));
            }
        }
        return false;
    }

    public static Map<String, Object> buildStartSizeMap(Map<String, Object> objectMap, int start, int size) {
        if (objectMap == null) {
            return buildStartSizeMap(start, size);
        }
        buildSizeMap(objectMap, DbFields.START_SQL_PARAMETER, start, size);
        return objectMap;
    }

    private static <M> Map<String, Object> buildSizeMap(Map<String, Object> objectMap, String key, M value, int size) {
        objectMap.put(key, value);
        objectMap.put(DbFields.SIZE_SQL_PARAMETER, size);
        return objectMap;
    }

    public static <M> Map<String, Object> buildStartIdSizeMap(Map<String, Object> objectMap, M startId, int size) {
        if (objectMap == null) {
            return buildStartIdSizeMap(startId, size);
        }
        buildOrder(objectMap, DbFields.ID_COLUMN, false);
        return buildSizeMap(objectMap, DbFields.START_ID_SQL_PARAMETER, startId, size);
    }

    public static <M> Map<String, Object> buildEndIdSizeMap(Map<String, Object> objectMap, M endId, int size) {
        if (objectMap == null) {
            return buildEndIdSizeMap(endId, size);
        }
        buildOrder(objectMap, DbFields.ID_COLUMN, true);
        return buildSizeMap(objectMap, DbFields.END_ID_SQL_PARAMETER, endId, size);
    }


    public static Map<String, Object> buildStartSizeMap(int start, int size) {
        Map<String, Object> ret = new HashMap<>();
        return buildStartSizeMap(ret, start, size);
    }

    public static Map<String, Object> buildStEtMap(Object ret, String st, String et, int start, int size) {
        if (ret == null) {
            return buildStEtMap(new HashMap<>(), et, et, start, size);
        }
        if (ret instanceof Map) {
            return buildStEtMap((Map<String, Object>) ret, et, st, start, size);
        }
        Map<String, Object> para = introspect(ret);
        return buildStEtMap(para, st, et, start, size);
    }

    public static Map<String, Object> buildStEtMap(Object ret, String st, String et) {
        return buildStEtMap(ret, st, et, 0, Integer.MAX_VALUE);
    }

    public static Map<String, Object> buildStEtMap(Map<String, Object> ret, String st, String et) {
        return buildStEtMap(ret, et, st, 0, Integer.MAX_VALUE);
    }

    public static Map<String, Object> buildStEtMap(Map<String, Object> ret, String st, String et, int start, int size) {
        ret.put(DbFields.ST_SQL_PARAMETER, st);
        ret.put(DbFields.ET_SQL_PARAMETER, et);
        return buildStartSizeMap(ret, start, size);
    }


    public static <M> Map<String, Object> buildStartIdSizeMap(M startId, int size) {
        Map<String, Object> ret = new HashMap<>();
        return buildStartIdSizeMap(ret, startId, size);
    }

    public static <M> Map<String, Object> buildEndIdSizeMap(M endId, int size) {
        Map<String, Object> ret = new HashMap<>();
        return buildEndIdSizeMap(ret, endId, size);
    }

    public static <M> Object buildPolymorphismTableMap(M id, String polymorphismTable) {
        Map<String, Object> para = new HashMap<>();
        para.put(DbFields.ID_SQL_PARAMETER, id);
        initTableNameToMap(para, polymorphismTable);
        return para;
    }

    public static <G> List<String> buildNoIgnoreZeroFieldList(G object) {
        List<String> zeroFieldList = new ArrayList<>();
        if (object instanceof Map) {
            return zeroFieldList;
        }
        if (object instanceof Collection) {
            return zeroFieldList;
        }
        Class<?> clazz = object.getClass();
        Class<?> superClazz = clazz.getSuperclass();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            log.error("Get bean info failed. This class: {}, Error msg:", clazz, e);
            return zeroFieldList;
        }
        BeanInfo superBeanInfo = null;
        try {
            superBeanInfo = Introspector.getBeanInfo(superClazz);
        } catch (IntrospectionException e) {
            log.error("Get bean info failed. This class: {}, Error msg:", superClazz, e);
        }
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        PropertyDescriptor[] superProps = (superBeanInfo == null) ? null : superBeanInfo.getPropertyDescriptors();
        if (props == null && superProps == null) {
            return zeroFieldList;
        }
        List<PropertyDescriptor> propList = props == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(props));
        List<PropertyDescriptor> superPropList = superProps == null ? new ArrayList<>() : Arrays.asList(superProps);
        if (!CollectionUtils.isEmpty(superPropList)) {
            propList.removeAll(superPropList);
        }
        initZeroFieldList(superPropList, superClazz, zeroFieldList);
        initZeroFieldList(propList, superClazz, zeroFieldList);
        return zeroFieldList;

    }

    private static void initZeroFieldList(List<PropertyDescriptor> propList, Class<?> clazz, List<String> zeroFieldList) {
        for (PropertyDescriptor propertyDescriptor : propList) {
            String name = propertyDescriptor.getName();
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                if (f.isAnnotationPresent(DbFieldConstraint.ZERO_ENABLE.class)) {
                    zeroFieldList.add(f.getName());
                }
            } catch (Exception e) {
                if (e instanceof NoSuchFieldException) {
                    continue;
                }
                log.error("Init class: {} zero field failed. Error msg: {}", clazz, e);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildStartSizeMap(Object object, int start, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildStartSizeMap((Map<String, Object>) object, start, size);
            }
            try {
                ret = buildDomainForQuery(object);
                buildStartSizeMap(ret, start, size);
                return ret;
            } catch (Exception e) {
                log.error("Change object:{} to map failed.Error msg:{}.", object, e);
                return buildStartSizeMap(start, size);
            }
        } else {
            return buildStartSizeMap(start, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M> Map<String, Object> buildObjectStartIdSizeMap(Object object, M startId, int size) throws BizException {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildStartIdSizeMap((Map<String, Object>) object, startId, size);
            }
            try {
                ret = buildDomainForQuery(object);
                return buildStartIdSizeMap(ret, startId, size);
            } catch (Exception e) {
                log.error("Change object:{} to map failed.Error msg:{}.", object, e);
                throw BizException.getInternalException(e.getMessage());
            }
        } else {
            return buildStartIdSizeMap(startId, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M> Map<String, Object> buildObjectEndIdSizeMap(Object object, M endId, int size) throws BizException {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildEndIdSizeMap((Map<String, Object>) object, endId, size);
            }
            try {
                ret = buildDomainForQuery(object);
                return buildEndIdSizeMap(ret, endId, size);
            } catch (Exception e) {
                log.error("Change object:{} to map failed.Error msg:{}.", object, e);
                throw BizException.getInternalException(e.getMessage());
            }
        } else {
            return buildEndIdSizeMap(endId, size);
        }
    }

    public static <M> Map<String, Object> buildIdsPara(List<M> ids) {
        Map<String, Object> para = new HashMap<>();
        para.put(DbFields.IDS_SQL_PARAMETER, ids);
        return para;
    }

    public static final Map<String, Object> buildExpressionPara(List<String> expressions) {
        Map<String, Object> para = new HashMap<>();
        para.put(DbFields.EXPRESSIONS_SQL_PARAMETER, expressions);
        return para;
    }

    public static Map<String, Object> buildStEtMap(String st, String et) {
        Map<String, Object> ret = new HashMap<>();
        buildStEtMap(ret, st, et);
        return ret;
    }

    public static String formatOrder(String order) {
        return EnumSqlOrderType.enumValueOf(order).getSql();
    }


    public static String formatLeftLike(String value) {
        return value == null ? "%%" : "%" + value;
    }

    public static String formatFullLike(String value) {
        return value == null ? "%%" : "%" + value + "%";
    }

    public static <M> Map<String, Object> buildShardingStartIdSizeMap(String tableSuffix, M startId, int size) {
        Map<String, Object> ret = buildStartIdSizeMap(startId, size);
        initTableSuffix(ret, tableSuffix);
        return ret;
    }

    public static Map<String, Object> initStartEndTime(Map<String, Object> para, String st, String et) {
        return buildStEtMap(para, st, et);
    }

    public static <M> Object buildIdShardingMap(M id, String tableSuffix) {
        return buildPolymorphismTableMap(id, tableSuffix);
    }


    public static Map<String, Object> introspectForUpdate(Object g) {
        return introspect(g, false, null);
    }

    public static <G> Map<String, Object> introspectForInsertOrUpdate(G g) {
        return introspect(g, false, null);
    }

    public static Map<String, Object> buildOrder(Map<String, Object> para, String orderColumn, boolean orderByDesc) {
        if (!CollectionUtils.isEmpty(para)) {
            para.put(DbFields.ORDER_COLUMN_SQL_PARAMETER, orderColumn);
            para.put(DbFields.ORDER_COLUMN_SORT_SQL_PARAMETER, orderByDesc ? EnumSqlOrderType.DESC.getSql() : EnumSqlOrderType.ASC.getSql());
        }
        return para;
    }

    public static Map<String, Object> introspect(Object obj) {
        return introspect(obj, false, null);
    }

    public static Map<String, Object> introspect(String tableSuffix, Object obj, boolean ignoreZero, List<String> noIgnoreZeroField) {
        Map<String, Object> result = introspect(obj, ignoreZero, noIgnoreZeroField);
        initTableSuffix(result, tableSuffix);
        return result;
    }

    public static Map<String, Object> initTableSuffix(String tableSuffix) {
        Map<String, Object> result = new HashMap<>();
        initPutTableSuffix(result, tableSuffix);
        return result;
    }

    public static <M> Object buildIdMap(M id) {
        Map<String, Object> result = new HashMap<>();
        result.put(DbFields.ID_COLUMN, id);
        return result;
    }
}
