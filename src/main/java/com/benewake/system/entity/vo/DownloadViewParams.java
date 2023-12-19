package com.benewake.system.entity.vo;

import com.benewake.system.entity.vo.baseParam.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class DownloadViewParams extends PageParam {
    Integer type;
    Integer tableId;
    Long viewId;
    List<ViewColParam> cols;
}
