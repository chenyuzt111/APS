package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan;
import com.benewake.system.entity.Interface.VersionToChVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Mapper
 * @createDate 2023-11-02 11:38:43
 * @Entity com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan
 */
@Mapper
public interface ApsSemiFinishedGoodsProductionPlanMapper extends BaseMapper<ApsSemiFinishedGoodsProductionPlan> {


    Page<Object> queryPageList(Page<Object> apsProductionPlanPage,
                               @Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                               @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> objectQueryWrapper,
                            @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);
}




