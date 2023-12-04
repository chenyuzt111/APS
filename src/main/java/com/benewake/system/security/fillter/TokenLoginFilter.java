package com.benewake.system.security.fillter;

import com.alibaba.fastjson2.JSON;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ResultCodeEnum;
import com.benewake.system.entity.vo.LoginVo;
import com.benewake.system.security.custom.CustomUser;
import com.benewake.system.service.LoginLogService;
import com.benewake.system.utils.IpUtil;
import com.benewake.system.utils.JwtHelper;
import com.benewake.system.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月04 11:00
 * 描 述： TOD
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
    private RedisTemplate redisTemplate;

    private LoginLogService loginLogService;

    public TokenLoginFilter(AuthenticationManager authenticationManager,
                            RedisTemplate redisTemplate, LoginLogService loginLogService) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/system/index/login","POST"));
        this.redisTemplate = redisTemplate;
        this.loginLogService = loginLogService;
    }

    /**
     * 获取用户名和密码，认证
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginVo loginVo =
                    new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 认证成功调用
     * @param request
     * @param response
     * @param chain
     * @param auth
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        System.out.println("成功");
        //获取认证对象
        CustomUser customUser = (CustomUser)auth.getPrincipal();

        //保存权限数据
        redisTemplate.opsForValue().set(customUser.getUsername(),
                JSON.toJSONString(customUser.getAuthorities()));

        //生成token
        String token =
                JwtHelper.createToken(customUser.getSysUser().getId(),
                        customUser.getSysUser().getUsername());

        //记录登录日志
        loginLogService.recordLoginLog(customUser.getUsername(),1,
                IpUtil.getIpAddress(request),"登录成功");
        //返回
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        ResponseUtil.out(response, Result.ok(map));
    }

    /**
     * 认证失败
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if(e.getCause() instanceof RuntimeException) {
            ResponseUtil.out(response, Result.build(null, 204, e.getMessage()));
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_MOBLE_ERROR));
        }
    }
}
