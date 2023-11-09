package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.ApsProductFamilyMachineTableDto;
import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ApsProductFamilyMachineTableVo extends PageParam {

    List<ApsProductFamilyMachineTableDto> apsProductFamilyMachineTables;

    public ApsProductFamilyMachineTableVo setPage(Page page) {
        this.setApsProductFamilyMachineTables(page.getRecords());
        this.setSize((int) page.getSize());
        this.setPages(page.getPages());
        this.setTotal(page.getTotal());
        this.setPage((int) page.getCurrent());
        return this;
    }
}
