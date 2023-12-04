package com.benewake.system.controller.intercepter;


import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.service.SysUserService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


/**
 * @author Lcs
 */
@Slf4j
@Component
public class LoginStatusInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String token = request.getHeader("token");
        log.info("token:-----" + token + "----" + LocalDateTime.now());
        if (token != null) {
            String username = JwtHelper.getUsername(token);
            // 检查凭证是否有效
            if (username != null) {
                // 根据凭证查询用户
                SysUser user = sysUserService.getUserInfoByUsername(username);
                // 设置用户拥有的角色信息
                user.setRoleList(sysRoleService.getUserRoles(user.getId()));
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获取当前用户
        SysUser user = hostHolder.getUser();
        if (null != user && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理当前用户
        hostHolder.clear();
    }
}
