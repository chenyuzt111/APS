package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsProductionPlanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Mapper
* @createDate 2023-11-02 11:38:43
* @Entity com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan
*/
@Mapper
public interface ApsSemiFinishedGoodsProductionPlanMapper extends BaseMapper<ApsSemiFinishedGoodsProductionPlan> {

    Page<ApsSemiFinishedGoodsProductionPlanDto> selectPageList(Page<ApsSemiFinishedGoodsProductionPlan> goodsProductionPlanPage, @Param("apsTableVersion") Integer apsTableVersion);
}




