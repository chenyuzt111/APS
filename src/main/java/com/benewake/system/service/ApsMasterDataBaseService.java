package com.benewake.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProcessNamePool;

import java.util.List;

public interface ApsMasterDataBaseService {

    Page selectPageLists(Page<Object> page, QueryWrapper<Object> wrapper);

    List<Object> searchLike(QueryWrapper<Object> queryWrapper);
}
