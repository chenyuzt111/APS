package com.benewake.system.entity.vo;

import com.benewake.system.entity.ColumnVo;
import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class ResultColPageVo<T> extends PageParam {
    private List<T> list;

    private List<ColumnVo> columnTables;
}
