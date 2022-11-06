package com.itmokers.kaleidoscope.mapper;

import com.itmokers.kaleidoscope.domain.DbDomain;
import com.itmokers.kaleidoscope.domain.exception.BizException;

import java.util.List;

public interface CreateMapper<M, G extends DbDomain<M>>{
    void insert(G g) throws BizException;

    void insert(List<G> gg) throws BizException;

    void insertOrUpdate(G g) throws BizException;

    void insertOrUpdate(List<G> gg) throws BizException;

    void insertOrIgnore(G g) throws BizException;

    void insertOrIgnore(List<G> gg) throws BizException;
}
