package com.benewake.system.KindDee;

import lombok.Data;

@Data
public class KingdeeDataSource {

    //("父项物料编码")
    private String fMaterialId;

    //("父项物料名称")
    private String fItemName;

    //("父项规格型号")
    private String fItemModel;

    //("子项物料编码")
    private String fMaterialIdChild;

    //("子项物料名称")
    private String fChildItemName;

    //("子项规格型号")
    private String fChildItemModel;
}