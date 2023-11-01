package com.benewake.system.entity.vo;

import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.vo.baseParam.PageParam;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
public class ApsProductFamilyMachineTableVo extends PageParam {
    List<ApsProductFamilyMachineTable> apsProductFamilyMachineTables;
}
