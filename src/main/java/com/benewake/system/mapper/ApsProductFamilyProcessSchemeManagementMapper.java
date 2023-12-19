package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.dto.ProcessSchemeManagementDto;
import com.benewake.system.entity.vo.ProcessSchemeManagementVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_product_family_process_scheme_management】的数据库操作Mapper
* @createDate 2023-10-24 11:38:49
* @Entity com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement
*/
@Mapper
public interface ApsProductFamilyProcessSchemeManagementMapper extends BaseMapper<ApsProductFamilyProcessSchemeManagement> {

    Page<ProcessSchemeManagementDto> selectPages(Page<Object> page, @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);
}




