package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.dto.ApsProductFamilyMachineTableDto;
import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ApsProductFamilyMachineTablePageVo extends PageParam {

    List<ApsProductFamilyMachineTableVo> apsProductFamilyMachineTables;

    public void setPage(Page page) {
        this.setSize((int) page.getSize());
        this.setPages(page.getPages());
        this.setTotal(page.getTotal());
        this.setPage((int) page.getCurrent());
    }
}
