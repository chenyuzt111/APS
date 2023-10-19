package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeImmediatelyInventory {


    //("物料编码")
    private String FMaterialId;
    //("物料编码")
    private String FMaterialName;
    //("仓库名称")
    private String FStockName;
    //("库存量(基本单位)")
    private int FBaseQty;

    //("可用量(主单位)")
    private int FAVBQty;
    //("批号")
    private String FLot;
    //("有效期至")
    private String FExpiryDate;
}