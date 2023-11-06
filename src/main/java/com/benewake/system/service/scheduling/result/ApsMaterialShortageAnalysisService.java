package com.benewake.system.service.scheduling.result;

import com.benewake.system.entity.ApsMaterialShortageAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.PageListRestVo;

/**
* @author ASUS
* @description 针对表【aps_material_shortage_analysis】的数据库操作Service
* @createDate 2023-11-02 11:39:08
*/
public interface ApsMaterialShortageAnalysisService extends IService<ApsMaterialShortageAnalysis> ,ApsSchedulingResuleBase{

    PageListRestVo<ApsMaterialShortageAnalysis> getAllPage(Integer page, Integer size);
}
