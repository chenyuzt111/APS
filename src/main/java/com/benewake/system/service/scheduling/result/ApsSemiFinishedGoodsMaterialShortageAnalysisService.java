package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsMaterialShortageAnalysisDto;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_material_shortage_analysis】的数据库操作Service
* @createDate 2023-11-02 11:39:24
*/
public interface ApsSemiFinishedGoodsMaterialShortageAnalysisService extends IService<ApsSemiFinishedGoodsMaterialShortageAnalysis> ,ApsSchedulingResuleBase{

    PageResultVo<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> getAllPage(Integer page, Integer size);

    ResultColPageVo<Object> semiMaterialShortageFiltrate(Integer page, Integer size , QueryViewParams queryViewParams);
}
