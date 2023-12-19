package com.benewake.system.service;

import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;

public interface MasterDataService {
    ResultColPageVo<Object> getFiltrateDate(Integer page, Integer size, QueryViewParams queryViewParams);
}
