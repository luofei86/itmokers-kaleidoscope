package com.itmokers.kaleidoscope.mapper;

import com.itmokers.kaleidoscope.domain.DbDomain;
import com.itmokers.kaleidoscope.exception.BizException;

import java.util.List;

public interface UpdateMapper<M, G extends DbDomain<M>> {

    void updateData(G g) throws BizException;

    void updateData(List<G> g) throws BizException;
}
