package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import com.benewake.system.mapper.ApsProductFamilyMachineTableMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Page<ApsProductFamilyMachineTable> tablePage = page(apsProductFamilyMachineTablePage, null);
        List<ApsProductFamilyMachineTable> records = tablePage.getRecords();
        long pages = tablePage.getPages();
        long total = tablePage.getTotal();
        ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo = new ApsProductFamilyMachineTableVo();
        apsProductFamilyMachineTableVo.setApsProductFamilyMachineTables(records);
        apsProductFamilyMachineTableVo.setSize(size);
        apsProductFamilyMachineTableVo.setPage(page);
        apsProductFamilyMachineTableVo.setPages(pages);
        apsProductFamilyMachineTableVo.setTotal(total);
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




