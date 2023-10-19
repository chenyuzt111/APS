package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.service.InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    private InterfaceService interfaceService;


    @ApiOperation("查询")
    @PostMapping("/getMultipleVersionsData")
    public Result getMultipleVersionsData(@PathParam("page") Integer page, @PathParam("size") Integer size, @PathParam("type") Integer type) {
        List<Object> apsResult = interfaceService.getMultipleVersionsData(page, size, type);
        return Result.ok(apsResult);
    }


}
