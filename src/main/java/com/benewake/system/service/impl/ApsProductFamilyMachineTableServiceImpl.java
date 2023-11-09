package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductFamilyMachineTableDto;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import com.benewake.system.mapper.ApsProductFamilyMachineTableMapper;
import org.springframework.stereotype.Service;

/**
* @author ASUS
* @description 针对表【aps_product_family_machine_table】的数据库操作Service实现
* @createDate 2023-10-31 10:48:29
*/
@Service
public class ApsProductFamilyMachineTableServiceImpl extends ServiceImpl<ApsProductFamilyMachineTableMapper, ApsProductFamilyMachineTable>
    implements ApsProductFamilyMachineTableService{

    @Override
    public ApsProductFamilyMachineTableVo getApsMachineTable(String name, Integer page, Integer size) {
        Page<ApsProductFamilyMachineTable> apsProductFamilyMachineTablePage = new Page<>();
        apsProductFamilyMachineTablePage.setCurrent(page);
        apsProductFamilyMachineTablePage.setSize(size);
        Page<ApsProductFamilyMachineTableDto> tablePage = baseMapper.getPage(apsProductFamilyMachineTablePage);
        ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo = new ApsProductFamilyMachineTableVo();
        apsProductFamilyMachineTableVo = apsProductFamilyMachineTableVo.setPage(tablePage);
        return apsProductFamilyMachineTableVo;
    }

    @Override
    public boolean addOrUpdateApsMachineTable(ApsProductFamilyMachineTable apsProductFamilyMachineTable) {
        boolean res;
        if (apsProductFamilyMachineTable.getId() == null) {
            res = save(apsProductFamilyMachineTable);
        } else {
            res = updateById(apsProductFamilyMachineTable);
        }
        return res;
    }
}




