package com.benewake.system.entity.kingdee;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class KingdeeMaterialBom {


    private String FNumber;

//    private String FUseOrgId;

    //("父项物料编码")
    private String FMaterialID;

    private String FITEMNAME;

    //("数据状态")
    private String FDocumentStatus;

    //("子项物料编码")
    private String FMaterialIDChild;
    //("子项物料编码")
    private String FCHILDITEMNAME;

    //("用量:分子")
    private String FNumerator;

    //("用量:分母")
    private String FDenominator;

    //("固定损耗")
    private String FFixScrapQtyLot;
    //子项类型
    private String FMaterialType;
    //替代方式
    private String FReplaceType;
    //项次
    private String FReplaceGroup;

    //("变动损耗率%")
    private String FScrapRate;

//    private String FForBidStatus;
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
//    private String FExpireDate;


}
