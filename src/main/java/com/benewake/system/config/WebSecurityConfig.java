package com.benewake.system.config;

import com.benewake.system.entity.Result;
import com.benewake.system.security.custom.CustomMd5Password;
import com.benewake.system.security.fillter.TokenAuthenticationFilter;
import com.benewake.system.security.fillter.TokenLoginFilter;
import com.benewake.system.service.LoginLogService;
import com.benewake.system.utils.JWTBlacklistManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月04 10:22
 * 描 述： TODO
 */
@Configuration
@EnableWebSecurity //@EnableWebSecurity是开启SpringSecurity的默认行为
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启注解功能，默认禁用注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //获取用户得详细信息的服务
    @Autowired
    private UserDetailsService userDetailsService;

    //自定义的密码加速器
    @Autowired
    private CustomMd5Password customMd5PasswordEncoder;

    //用于操作Redis缓存的模板
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private JWTBlacklistManager jwtBlacklistManager;

    //配置认证管理器定义为Bean
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 这是配置的关键，决定哪些接口开启防护，哪些接口绕过防护
        http
                // 实际应用再开启csrf
                .csrf().disable()
                // 开启跨域以便前端调用接口
                .cors().and()

                .authorizeRequests()
                // 指定某些接口不需要通过验证即可访问。登陆接口肯定是不需要认证的
                .antMatchers("/admin/system/index/login").permitAll()
                // 任何的接口都需要认证，也就是需要登陆才能访问
                .anyRequest().authenticated()
                .and()
                //TokenAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面，
                //这样做就是为了除了登录的时候去查询数据库外，其他时候都用token进行认证。
                .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate,loginLogService));
                /**
                 * lcs:
                 *                  单个用户只能创建一个session，即只能在服务器登录一次
                 *                  springsecurity的实现是新来一个session后 获取该session的token中的用户名
                 *                  然后遍历所有session 判断这个用户名的数量是否超过maximumSessions
                 *                  如果超过的话就会删除 最早的那个session
                 *                  但如果被删除的session再次携带那个token进入的话又会挤掉当前的session
                 *                  如果需要开启 可以考虑 被挤掉后 前端header中删除token  需再次登录生成新的token才能进入
                 *
                 *                  如果需要在集群环境下 限制用户登录的话请结合redis实现
                 */
//                http.sessionManagement().maximumSessions(1)
                            // 是否禁止再次登录
//                        .maxSessionsPreventsLogin(false)
//                        .expiredSessionStrategy(event->{
//                            HttpServletResponse response = event.getResponse();
//                            response.setContentType("application/json;charset=UTF-8");
//                            Map<String,Object> map = new HashMap<>();
//                            map.put("code",500);
//                            map.put("message","当前会话已失效，请重新登录！");
//                            map.put("data",null);
//                            String s = new ObjectMapper().writeValueAsString(map);
//                            response.getWriter().write(s);
//                            response.flushBuffer();
//                        });
                http.logout().logoutUrl("/system/index/logout")
                .deleteCookies("JSESSIONID");

        //禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 指定UserDetailService和加密器
        auth.userDetailsService(userDetailsService).passwordEncoder(customMd5PasswordEncoder);
    }

    /**
     * 配置哪些请求不拦截
     * 排除swagger相关请求
     * @param web
     * @throws
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico","/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/doc.html");
    }
}
