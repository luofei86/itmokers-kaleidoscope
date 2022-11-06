package com.itmokers.kaleidoscope.domain;

import com.itmokers.kaleidoscope.model.DbFieldConstraint;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DbDomain<M> implements Serializable {
    private M id;
    @DbFieldConstraint.ZERO_ENABLE
    @DbFieldConstraint.SELECT_ALL_KEY
    private int delFlag;
    private Date updateTime;
    private Date createTime;

}
