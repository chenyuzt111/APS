package com.benewake.system.service;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_immediately_inventory】的数据库操作Service
* @createDate 2023-10-13 10:11:17
*/
public interface ApsImmediatelyInventoryService extends IService<ApsImmediatelyInventory> ,KingdeeService{

    List<com.benewake.system.entity.Interface.ApsImmediatelyInventory> getApsImmediatelyInventory(Integer page, Integer size);
}
