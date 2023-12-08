package com.benewake.system.service.query.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benewake.system.service.query.strategy.QueryStrategy;

public class GreaterThanOrEqualStrategy implements QueryStrategy {
    @Override
    public void apply(QueryWrapper<Object> queryWrapper, String enColName, String colValue) {
        queryWrapper.ge(enColName, colValue);
    }
}