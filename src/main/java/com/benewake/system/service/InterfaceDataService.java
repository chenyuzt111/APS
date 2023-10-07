package com.benewake.system.service;


import com.benewake.system.entity.vo.GetCacheByTypeVo;

import java.util.List;

public interface InterfaceDataService {
    Boolean updateData(List<Integer> ids) throws Exception;
}
