package com.itmokers.kaleidoscope.mapper;

import com.itmokers.kaleidoscope.domain.DbDomain;
import com.itmokers.kaleidoscope.exception.BizException;

import java.util.List;

public interface DeleteMapper<M, G extends DbDomain<M>> {

    void updateDelFlag(G g) throws BizException;

    void updateDelFlag(List<G> g) throws BizException;
}
