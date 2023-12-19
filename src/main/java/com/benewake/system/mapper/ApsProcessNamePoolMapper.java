package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.vo.ApsProcessNamePoolVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author ASUS
 * @description 针对表【aps_process_name_pool】的数据库操作Mapper
 * @createDate 2023-10-20 09:20:16
 * @Entity com.benewake.system.entity.ApsProcessNamePool
 */
@Mapper
public interface ApsProcessNamePoolMapper extends BaseMapper<ApsProcessNamePool> {

    Page<ApsProcessNamePoolVo> selectPages(Page<Object> page, @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);
}




