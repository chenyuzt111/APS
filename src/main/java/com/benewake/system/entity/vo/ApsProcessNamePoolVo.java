package com.benewake.system.entity.vo;

import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ApsProcessNamePoolVo extends PageParam {
    List<ApsProcessNamePool> apsProcessNamePools;
}
