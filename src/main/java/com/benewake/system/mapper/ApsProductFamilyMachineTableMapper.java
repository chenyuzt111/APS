package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.dto.ApsProductFamilyMachineTableDto;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ASUS
* @description 针对表【aps_product_family_machine_table】的数据库操作Mapper
* @createDate 2023-10-31 10:48:29
* @Entity com.benewake.system.entity.dto.ApsProductFamilyMachineTableDto
*/
@Mapper
public interface ApsProductFamilyMachineTableMapper extends BaseMapper<ApsProductFamilyMachineTable> {

    Page<ApsProductFamilyMachineTableDto> getPage(Page<ApsProductFamilyMachineTableDto> apsProductFamilyMachineTablePage);
}




