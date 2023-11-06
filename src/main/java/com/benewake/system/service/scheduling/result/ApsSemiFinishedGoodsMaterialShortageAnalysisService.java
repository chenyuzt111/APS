package com.benewake.system.service.scheduling.result;

import com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.PageListRestVo;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_material_shortage_analysis】的数据库操作Service
* @createDate 2023-11-02 11:39:24
*/
public interface ApsSemiFinishedGoodsMaterialShortageAnalysisService extends IService<ApsSemiFinishedGoodsMaterialShortageAnalysis> ,ApsSchedulingResuleBase{

    PageListRestVo<ApsSemiFinishedGoodsMaterialShortageAnalysis> getAllPage(Integer page, Integer size);
}
