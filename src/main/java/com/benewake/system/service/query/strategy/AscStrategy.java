package com.benewake.system.service.query.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class AscStrategy implements QueryStrategy {
    @Override
    public void apply(QueryWrapper<Object> queryWrapper, String enColName, String colValue) {
        queryWrapper.orderByAsc(enColName);
    }
}