package com.benewake.system.security.fillter;

import com.alibaba.fastjson.JSON;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ResultCodeEnum;
import com.benewake.system.utils.JwtHelper;
import com.benewake.system.utils.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月04 11:18
 * 描 述： TOD
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    //定义了一个名为RedisTemplate的成员变量，用于和redis进行交互
    private RedisTemplate redisTemplate;


    //构造函数接收一个redisTemplate作为参数分配给成员变量
    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //对doFilterInternal方法的覆盖
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.info("uri:" + request.getRequestURI());
        //这里检查请求的URI是否与"/system/index/login"相匹配，如果匹配，就直接放行请求，不进行后续的身份验证处理。
        if ("/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        //这里检查请求的URI是否与"/prod-api/admin/system/index/login"相匹配，如果匹配，也直接放行请求，不进行后续的身份验证处理。
        if ("/prod-api/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        //调用getAuthentication方法来获取身份验证令牌
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication) {
            //不为空的时候说明成功获取到了身份验证令牌
            //将令牌设置到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            //没有令牌返回没有权限
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    //定义的私有方法主要作用是获取身份验证令牌
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:" + token);
        //如果获取到的token存在的话，从令牌中解析出用户名
        if (!StringUtils.isEmpty(token)) {
            String useruame = JwtHelper.getUsername(token);
            logger.info("useruame:" + useruame);
            if (!StringUtils.isEmpty(useruame)) {
                //如果存在用户名的话，使用redisTemplate从redis中获取存储到redis中的用户权限信息，这些权限通常以字符串的形式存储在redis中，在这里获取
                String authoritiesString =
                        (String) redisTemplate.opsForValue().get(useruame);
                //使用json工具将从redis取出的权限信息，将其转换为List<Map>对象
                List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                //遍历取出的权限信息将其转化为SimpleGrantedAuthority类型的对象，并添加到authorities列表中
                //SimpleGrantedAuthority类通常用于表示用户的角色或权限
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }
                //最后将解析出的用户名和权限列表创建一个UsernamePasswordAuthenticationToken对象表示身份验证成功，并将其返回
                return new UsernamePasswordAuthenticationToken(useruame, null, authorities);
            }
        }
        return null;
    }
}
