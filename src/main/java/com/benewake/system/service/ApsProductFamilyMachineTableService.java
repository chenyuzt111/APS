package com.benewake.system.service;

import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;

/**
* @author ASUS
* @description 针对表【aps_product_family_machine_table】的数据库操作Service
* @createDate 2023-10-31 10:48:29
*/
public interface ApsProductFamilyMachineTableService extends IService<ApsProductFamilyMachineTable> {

    ApsProductFamilyMachineTablePageVo getApsMachineTable(String name, Integer page, Integer size);

    boolean addOrUpdateApsMachineTable(ApsProductFamilyMachineTable apsProductFamilyMachineTable);
}
