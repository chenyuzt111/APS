package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeInventoryLock {

    //("字段物料编码")
    private String FMaterialId ;

    //("到期日")
    private String FEXPIRYDATE  ;

    //("锁库数量")
    private String FLockQty;

}