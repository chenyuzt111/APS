package com.benewake.system.mapper;

import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.vo.ProcessSchemeManagementDo;
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

    List<ProcessSchemeManagementVo> selectAllPage(Integer pass, Integer size);
}




