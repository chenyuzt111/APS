package com.benewake.system.controller;

import com.benewake.system.service.message.SseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Api(tags = "消息连接")
@RestController
@RequestMapping("/sse")
public class SseController {

    @Autowired
    private SseService sseService;

    @ApiOperation("sse链接")
    @GetMapping("/connect")
    public SseEmitter connect() {
        return sseService.connect();
    }

    @PostMapping("/send/{user}")
    public void data(@PathVariable("user") String username) throws Exception {
//        sseService.sendMessage(username ,"111");
    }
}
