package com.benewake.system.service.query.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface QueryStrategy {
    void apply(QueryWrapper<Object> queryWrapper, String enColName, String colValue);
}
