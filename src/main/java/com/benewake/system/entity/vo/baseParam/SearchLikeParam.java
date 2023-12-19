package com.benewake.system.entity.vo.baseParam;

import lombok.Data;

@Data
public class SearchLikeParam {
    private Integer tableId;
    private Integer colId;
    private String value;
}
