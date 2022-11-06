//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.itmokers.kaleidoscope.service;

import com.itmokers.kaleidoscope.domain.DbDomain;
import com.itmokers.kaleidoscope.exception.BizException;
import com.itmokers.kaleidoscope.mapper.CommonCrudMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * 所有带directFromDb的都是不论数据是否有效
 *
 * @author luofei
 * Generate 2020/1/13
 */
public interface CommonDataService<M, G extends DbDomain<M>> {
    CommonCrudMapper<M, G> getCommonDao(G g);

    default List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws BizException {
        return getCommonDao(g).queryListAll(g, st, et, start, size);
    }

    default long countDirectFromDb(G g) throws BizException {
        return getCommonDao(g).countAll(g);
    }

    default long countDirectFromDb(G g, String st, String et) throws BizException {
        if (StringUtils.isAllEmpty(st, et)) {
            return countDirectFromDb(g);
        }
        return getCommonDao(g).countAll(g, st, et);
    }

    default List<G> listDirectFromDb(G g, String st, String et, int start, int size, String orderColumn,
                                     boolean orderByDesc) throws BizException {

        return getCommonDao(g).queryListAll(g, st, et, start, size, orderColumn, orderByDesc);
    }

    default List<G> listDirectFromDb(G g, int start, int size, String orderColumn,
                                     boolean orderByDesc) throws BizException {
        return getCommonDao(g).queryListAll(g, start, size, orderColumn, orderByDesc);
    }

    default List<G> listDirectFromDb(G g, int start, int size) throws BizException {
        return getCommonDao(g).queryListAll(g, start, size);
    }

    default List<G> listDirectFromDb(G g) throws BizException {
        return getCommonDao(g).queryListAll(g);
    }

    default List<G> listDirectFromDb(G g, String st, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, st, null, start, size);
    }

    default List<G> listByStartIdDirectFromDb(G g, M startId, int size) throws BizException {
        return getCommonDao(g).queryListByStartIdAll(g, startId, size);
    }

    default List<G> listByEndIdDirectFromDb(G g, M endId, int size) throws BizException {
        return getCommonDao(g).queryListByEndIdAll(g, endId, size);
    }

    //########Only valid data will query

    default List<G> listFromDb(G g, String st, String et, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, st, et, start, size);
    }

    default long countFromDb(G g) throws BizException {
        return getCommonDao(g).count(g);
    }

    default long countFromDb(G g, String st, String et) throws BizException {
        return getCommonDao(g).count(g, st, et);
    }

    default List<G> listFromDb(G g, String st, String et, int start, int size, String orderColumn,
                               boolean orderByDesc) throws BizException {
        return getCommonDao(g).queryList(g, st, et, start, size, orderColumn, orderByDesc);
    }

    default List<G> listFromDb(G g, int start, int size, String orderColumn,
                               boolean orderByDesc) throws BizException {
        return getCommonDao(g).queryList(g, start, size, orderColumn, orderByDesc);
    }

    default List<G> listFromDb(G g, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, start, size);
    }

    default List<G> listFromDb(G g) throws BizException {
        return getCommonDao(g).queryList(g);
    }

    default List<G> listFromDb(G g, String st, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, st, null, start, size);
    }

    default List<G> listByStartIdFromDb(G g, M startId, int size) throws BizException {
        return getCommonDao(g).queryListByStartId(g, startId, size);
    }

    default List<G> listByEndIdFromDb(G g, M endId, int size) throws BizException {
        return getCommonDao(g).queryListByEndId(g, endId, size);
    }

    //query by id
    default G getFromDb(M id) throws BizException {
        return getCommonDao(null).queryObjectById(id);
    }

    default List<G> getDirectFromDb(List<M> ids) throws BizException {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return getCommonDao(null).queryList(ids);
    }

    //#######################CREATE OR MODIFY

    default void addData(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeToDb(g);
        getCommonDao(g).insert(g);
        afterToDb(g);
    }

    default void addDataOrUpdate(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeToDb(g);
        getCommonDao(g).insertOrUpdate(g);
        afterToDb(g);
    }

    default void addDataOrIgnore(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeToDb(g);
        getCommonDao(g).insertOrIgnore(g);
        afterToDb(g);
    }

    default void batchAdd(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insert(gg);
        afterToDb(gg);
    }


    default void batchAddOrIgnore(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrIgnore(gg);
        afterToDb(gg);
    }

    default void batchAddOrUpdate(List<G> gg) throws BizException {

        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrUpdate(gg);
        afterToDb(gg);
    }

    default void afterToDb(G g) throws BizException {
    }

    default void afterToDb(List<G> gg) throws BizException {
        for (G g : gg) {
            afterToDb(g);
        }
    }

    default void beforeToDb(G g) throws BizException {
    }

    default void beforeToDb(List<G> gg) throws BizException {
        for (G g : gg) {
            beforeToDb(g);
        }
    }

    default void modifyData(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeModifyData(g);
        getCommonDao(g).updateDelFlag(g);
        afterModifyData(g);
    }

    default void modifyData(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeModifyData(gg);
        getCommonDao(gg.get(0)).updateDelFlag(gg);
        afterModifyData(gg);
    }


    default void modifyStatus(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeModifyStatus(gg);
        getCommonDao(gg.get(0)).updateDelFlag(gg);
        afterModifyStatus(gg);
    }

    default void modifyStatus(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeModifyStatus(g);
        getCommonDao(g).updateDelFlag(g);
        afterModifyStatus(g);
    }


    default void afterModifyStatus(G g) {
    }

    default void beforeModifyStatus(G g) {
    }
    default void afterModifyStatus(List<G> gg) {
        for (G g : gg) {
            afterModifyStatus(g);
        }
    }


    default void beforeModifyStatus(List<G> gg) {
        for (G g : gg) {
            beforeModifyStatus(g);
        }
    }

    default void beforeModifyData(List<G> gg) {
        for (G g : gg) {
            beforeModifyData(g);
        }
    }

    default void beforeModifyData(G g) {
    }
    default void afterModifyData(List<G> gg) {
        for (G g : gg) {
            afterModifyData(g);
        }
    }

    default void afterModifyData(G g) {
    }
}
