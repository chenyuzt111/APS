package com.benewake.system.service.impl;

import com.benewake.system.security.custom.CustomUser;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.SysMenuService;
import com.benewake.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月04 10:54
 * 描 述： TOD
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserInfoByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在！");
        }
        if(sysUser.getStatus().equals(0)){
            throw new RuntimeException("用户被禁用了！请联系管理员");
        }

        // 根据用户id
        Map<String, Object> userMenuAndButtonList = sysMenuService.getUserMenuAndButtonList(sysUser.getId());
        List<String> permissions = (List<String>) userMenuAndButtonList.get("permsList");
        // 转换格式
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        permissions.forEach(p->{
            authorities.add(new SimpleGrantedAuthority(p.trim()));
        });
        return new CustomUser(sysUser, authorities);
    }
}
