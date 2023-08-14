package com.benewake.system.utils;


import com.benewake.system.entity.system.SysUser;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象。
 * @author Lcs
 */
@Component
public class HostHolder {

    private ThreadLocal<SysUser> users = new ThreadLocal<>();

    public void setUser(SysUser user) {
        users.set(user);
    }

    public SysUser getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
