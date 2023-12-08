package com.benewake.system.entity.vo;

import com.benewake.system.entity.ApsColumnTable;
import lombok.Data;

import java.util.List;

@Data
public class ViewParam {
    Long tableId;
    Long viewId;
    String viewName;
    List<ViewColParam> cols;
}
