package com.benewake.system.entity.kingdee.transfer;

import lombok.Data;

@Data
public class MaterialBomChange {
    //("审核日期")
    private String FApproveDate;

    //("变更标识")
    private String FChangeLabel;

    //("BOM版本")
    private String FBomVersion;

    //("父项物料编码")
    private String FParentMaterialId;

    //("子项物料编码")
    private String FMATERIALIDCHILD;

    //("子项物料名称")
    private String FChildItemName;
}