package com.benewake.system.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class QueryViewParams {
    Long tableId;
    Long viewId;
    List<ViewColParam> cols;
}
