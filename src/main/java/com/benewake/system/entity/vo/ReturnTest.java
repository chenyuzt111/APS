package com.benewake.system.entity.vo;

import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.benewake.system.entity.ApsProductionPlan;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
public class ReturnTest {

    Set<String> list;

    HashMap<String, List<ApsProductionPlan>> map;
}
