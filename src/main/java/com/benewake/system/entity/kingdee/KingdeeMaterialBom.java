package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeMaterialBom {


    private String FNumber;

//    private String FUseOrgId;

    //("父项物料编码")
    private String fMaterialID;

    private String fitemName;

    private String fItemModel;

    //("数据状态")
    private String fDocumentStatus;

    //("子项物料编码")
    private String fMaterialIDChild;
    //("子项物料编码")
//    private String FCHILDITEMNAME;
    private String FCHILDITEMNAME;

    private String fChildItemModel;
    //("用量:分子")
    private String fNumerator;

    //("用量:分母")
    private String fDenominator;

    //("固定损耗")
    private String fFixScrapQtyLot;
    //子项类型
    private String fMaterialType;
    //替代方式
    private String fReplaceType;
    //项次
    private String fReplaceGroup;

    //("变动损耗率%")
    private String fScrapRate;


//    private String FForBidStatus;
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
//    private String FExpireDate;


}
