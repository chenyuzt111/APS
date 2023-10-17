package com.benewake.system.controller;

import com.benewake.system.annotation.Scheduling;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.service.message.SseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.websocket.server.PathParam;
import java.util.List;

@Api(tags = "消息连接")
@RestController
//@Controller
@RequestMapping("/sse")
public class SsesController {

    @Autowired
    private SseService sseService;

    @ApiOperation("sse链接")
    @GetMapping("/connect")
    public SseEmitter connect() {
        return sseService.connect();
    }

    @PostMapping("/send/{user}")
    public void data(@PathVariable("user") String username) throws Exception {
        sseService.sendMessage(username ,"111");
    }
}
