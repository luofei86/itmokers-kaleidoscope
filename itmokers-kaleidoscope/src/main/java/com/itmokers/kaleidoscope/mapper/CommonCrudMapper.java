package com.itmokers.kaleidoscope.mapper;

import com.itmokers.kaleidoscope.domain.DbDomain;

public interface CommonCrudMapper<M, G extends DbDomain<M>> extends CreateMapper<M, G>,
        ResearchMapper<M, G>, UpdateMapper<M, G>, DeleteMapper<M, G> {
}
