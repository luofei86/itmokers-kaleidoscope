package com.itmokers.kaleidoscope.mapper;

import com.itmokers.kaleidoscope.domain.DbDomain;
import com.itmokers.kaleidoscope.domain.DbFields;
import com.itmokers.kaleidoscope.domain.exception.BizException;
import com.itmokers.kaleidoscope.utils.NormalLocalUtils;
import com.itmokers.kaleidoscope.utils.SqlParameterUtils;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;

public interface ResearchMapper<M, G extends DbDomain<M>> {
    default long count(G g) throws BizException {
        return count(g, true);
    }

    /**
     * @param g g
     * @return 数据元素数量
     * @throws BizException 运行异常
     */
    default long countAll(G g) throws BizException {
        return count(g, false);
    }


    /**
     * @param g         g
     * @param onlyValid 仅查询有效元素
     * @return 数据元素数量
     * @throws BizException 运行异常
     */
    default long count(G g, boolean onlyValid) throws BizException {
        if (g == null) {
            return 0;
        }
        Map<String, Object> para = SqlParameterUtils.buildDomainForQuery(g);
        return countByMap(para, onlyValid);
    }

    default long count(G g, String st, String et) throws BizException {
        return count(g, st, et, true);
    }


    default long countAll(G g, String st, String et) throws BizException {
        return count(g, st, et, false);
    }

    default long count(G g, String st, String et, boolean onlyValid) throws BizException {
        if (StringUtils.isAllEmpty(st, et)) {
            return count(g);
        }
        Map<String, Object> para = SqlParameterUtils.buildStEtMap(g, st, et);
        return countByMap(para, onlyValid);
    }

    default List<G> queryList(G g, int start, int size) throws BizException {
        return queryList(g, start, size, true);
    }

    default List<G> queryList(G g) throws BizException {
        return queryList(g, 0, Integer.MAX_VALUE, true);
    }

    default List<G> queryListAll(G g) throws BizException {
        return queryListAll(g, 0, Integer.MAX_VALUE);
    }

    default List<G> queryListAll(G g, int start, int size) throws BizException {
        return queryList(g, start, size, false);
    }


    default List<G> queryList(G g, int start, int size, boolean onlyValid) throws BizException {
        return queryList(g, start, size, DbFields.ID_COLUMN, true, onlyValid);
    }

    default List<G> queryList(G g, int start, int size, String orderColumn, boolean orderByDesc) throws BizException {
        return queryList(g, start, size, orderColumn, orderByDesc, true);
    }

    default List<G> queryListAll(G g, int start, int size, String orderColumn, boolean orderByDesc) throws BizException {
        return queryList(g, start, size, orderColumn, orderByDesc, false);
    }

    default List<G> queryList(G g, int start, int size, String orderColumn, boolean orderByDesc, boolean onValid) throws BizException {
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildOrder(SqlParameterUtils.buildStartSizeMap(g, start, size), orderColumn, orderByDesc), onValid);
    }


    default List<G> queryListByStartId(G g, M startId, int size) throws BizException {
        return queryListByStartId(g, startId, size, true);
    }

    default List<G> queryListByStartIdAll(G g, M startId, int size) throws BizException {
        return queryListByStartId(g, startId, size, false);
    }

    default List<G> queryListByStartId(G g, M startId, int size, boolean onlyValid) throws BizException {
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildObjectStartIdSizeMap(g, startId, size), onlyValid);
    }

    default List<G> queryListByEndId(G g, M endId, int size) throws BizException {
        return queryListByEndId(g, endId, size, true);
    }


    default List<G> queryListByEndIdAll(G g, M endId, int size) throws BizException {
        return queryListByEndId(g, endId, size, false);
    }

    default List<G> queryListByEndId(G g, M endId, int size, boolean onlyValid) throws BizException {
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildObjectEndIdSizeMap(g, endId, size), onlyValid);
    }

    default List<G> queryList(G g, String st, String et, int start, int size) throws BizException {
        return queryList(g, st, et, start, size, true);
    }


    default List<G> queryListAll(G g, String st, String et, int start, int size) throws BizException {
        return queryList(g, st, et, start, size, false);
    }


    default List<G> queryList(G g, String st, String et, int start, int size, boolean onlyValid) throws BizException {
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildStEtMap(g, st, et, start, size), onlyValid);
    }

    default List<G> queryList(G g, String st, String et, int start, int size, String orderColumn, boolean orderByDesc) throws BizException {
        return queryList(g, st, et, start, size, orderColumn, orderByDesc, true);
    }

    default List<G> queryListAll(G g, String st, String et, int start, int size, String orderColumn, boolean orderByDesc) throws BizException {
        return queryList(g, st, et, start, size, orderColumn, orderByDesc, false);
    }

    default List<G> queryList(G g, String st, String et, int start, int size, String orderColumn, boolean orderByDesc, boolean onlyValid) throws BizException {
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildOrder(SqlParameterUtils.buildStEtMap(g, st, et, start, size), orderColumn, orderByDesc), false);
    }

    default List<G> queryListByMapSizeMayNoLimit(Map<String, Object> para, boolean onlyValid) throws BizException {
        if (onlyValid) {
            para.put(DbFields.DEL_FLAG_ATTRIBUTE, DbFields.DEL_FLAG_OK);
        } else {
            para.remove(DbFields.DEL_FLAG_ATTRIBUTE);
        }
        return queryListByMapSizeMayNoLimit(para);
    }

    default List<G> queryList(List<M> ids) throws BizException {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        int parallelSize = DbFields.MAX_QUERY_LIST_SIZE;
        if (ids.size() > parallelSize) {
            ids = new ArrayList<>(new HashSet<>(ids));
            List<List<M>> subIdsList = NormalLocalUtils.splitList(ids, parallelSize);
            return parallelQueryList(subIdsList);
        }
        return queryListByMapSizeMayNoLimit(SqlParameterUtils.buildIdsPara(ids));
    }


    default List<G> parallelQueryList(List<List<M>> idsList) throws BizException {
        if (CollectionUtils.isEmpty(idsList)) {
            return new ArrayList<>();
        }

        List<Future<List<G>>> futureList = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(DbFields.THREADS);
        for (List<M> ids : idsList) {
            Future<List<G>> future = es.submit(() -> queryList(ids));
            futureList.add(future);
        }
        List<G> result = new ArrayList<>();
        try {
            for (Future<List<G>> future : futureList) {
                List<G> innerResult = null;
                try {
                    innerResult = future.get();
                } catch (Exception e) {
                    MapperLogger.LOGGER.error("Get data list failed. Ids: {}, Error: {}", idsList, e);
                    throw BizException.getSqlException(e.getMessage());
                }
                if (innerResult != null && innerResult.size() > 0) {
                    result.addAll(innerResult);
                }
            }
        } finally {
            es.shutdown();
        }
        return result;
    }


    default long countByMap(@NotNull Map<String, Object> para, boolean onlyValid) throws BizException {
        if (onlyValid) {
            para.put(DbFields.DEL_FLAG_ATTRIBUTE, DbFields.DEL_FLAG_OK);
        } else {
            para.remove(DbFields.DEL_FLAG_ATTRIBUTE);
        }
        return countByMap(para);
    }

    G queryObjectById(M id) throws BizException;

    default List<G> queryListByMapSizeMayNoLimit(Map<String, Object> para) throws BizException {
        List<G> result = new ArrayList<>();
        if (para.containsKey(DbFields.SIZE_SQL_PARAMETER)) {
            int querySize = (int) para.get(DbFields.SIZE_SQL_PARAMETER);
            if (querySize > DbFields.MAX_QUERY_LIST_SIZE) {
                Map<String, Object> innerPara = new HashMap<>(para);
                innerPara.put(DbFields.SIZE_SQL_PARAMETER, DbFields.MAX_QUERY_LIST_SIZE);
                do {
                    List<G> innerResult = queryListByMap(innerPara);
                    if (innerResult == null) {
                        break;
                    }
                    result.addAll(innerResult);
                    if (innerResult.size() < DbFields.MAX_QUERY_LIST_SIZE) {
                        break;
                    }
                } while (true);
                return result;
            } else {
                return queryListByMap(para);
            }
        } else {
            return queryListByMap(para);
        }
    }

    List<G> queryListByMap(Map<String, Object> para);


    long countByMap(Map<String, Object> para) throws BizException;
}
