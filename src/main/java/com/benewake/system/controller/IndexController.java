package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.LoginVo;
import com.benewake.system.service.SysUserService;
import com.benewake.system.utils.JwtHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月01 16:44
 * 描 述： TODO
 */
@Api(tags = "后台管理员登录接口")
@RestController
@RequestMapping("/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/")
    public Result tIndex() {
        return Result.ok().message("index！！");
    }


    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        // 此登录部分代码可以无视，已使用springsecurity进行认证操作
        Map<String, Object> map = sysUserService.login(loginVo);
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            // 验证成功 存入token
            return Result.ok(map);
        }
    }

    @ApiOperation("用户退出接口")
    @PostMapping("exit")
    public Result exit(HttpServletRequest request) {
        // 获取请求头token字符串
        String token = request.getHeader("token");
        return sysUserService.loglogoutin(token) ? Result.ok() : Result.fail();
    }


    @ApiOperation("获取登录用户的用户信息接口")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        // 获取请求头token字符串
        String token = request.getHeader("token");
        // 获取用户名称或id
        String id = JwtHelper.getUserId(token);
        String username = JwtHelper.getUsername(token);
        // 获取用户信息
        Map<String, Object> map = sysUserService.getUserInfo(id, username);

        return Result.ok(map);
    }




    /* spring security 学习测试部分 */

    /**
     * 只有参数中 name==认证的name的请求才会通过
     *
     * @param name
     * @return
     */
    @PreAuthorize("authentication.name==#name")
    @GetMapping("/tname")
    public String tHello(String name) {
        return "hello:" + name;
    }

    /**
     * filterTarget 必须是数组 集合类型
     * filterObject 是 filterTarget 中的对象
     *
     * @param users
     */
    @PreFilter(value = "filterObject.id%2!=0", filterTarget = "users")
    @PostMapping("/tusers")
    public void tAddUsers(@RequestBody List<SysUser> users) {
        users.forEach(System.out::println);
    }

    /**
     * 只返回结果中id为1的数据
     *
     * @param id
     * @return
     */
    @PostAuthorize("returnObject.id=1")
    @GetMapping("/tuserId")
    public SysUser tgetUserById(String id) {
        return new SysUser();
    }

    /**
     * 只返回结果符合条件的数据  返回结果需要是 数组 集合类型
     *
     * @return
     */
    @PostFilter("filterObject.id%2==0")
    @GetMapping("/tlists")
    public List<SysUser> tgetAll() {
        List<SysUser> users = new ArrayList<>();
        for (int i = 1; i <= 10; ++i) {
            SysUser user = new SysUser();
            user.setId(String.valueOf(i));
            users.add(user);
        }
        return users;
    }
}
