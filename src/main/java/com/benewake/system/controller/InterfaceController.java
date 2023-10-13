package com.benewake.system.controller;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.Result;
import com.benewake.system.service.ApsImmediatelyInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@Api(tags = "接口数据")
@RestController
@RequestMapping("/interface")
public class InterfaceController {

    @Autowired
    private ApsImmediatelyInventoryService apsImmediatelyInventoryService;


    @ApiOperation("查询")
    @PostMapping("/getApsImmediatelyInventory")
    public Result getApsImmediatelyInventory(@PathParam("page") Integer page, @PathParam("size") Integer size) {
        List<com.benewake.system.entity.Interface.ApsImmediatelyInventory> apsImmediatelyInventory = apsImmediatelyInventoryService.getApsImmediatelyInventory(page, size);
        return Result.ok(apsImmediatelyInventory);
    }

}
