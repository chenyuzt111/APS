package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.annotation.DataScope;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.LoginVo;
import com.benewake.system.entity.vo.UpdatePwdVo;
import com.benewake.system.mapper.SysUserMapper;
import com.benewake.system.service.SysMenuService;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.service.SysUserService;
import com.benewake.system.utils.CommonUtils;
import com.benewake.system.utils.JwtHelper;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lcs
 * @since 2023-08-01 06:36:31
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUser(SysUser sysUser) {
        return baseMapper.selectUser(sysUser);
    }

    @Override
    public boolean updateStatus(String id, Integer status) {
        LambdaUpdateWrapper<SysUser> luw = new LambdaUpdateWrapper<>();
        luw.eq(SysUser::getId,id)
                .set(SysUser::getStatus,status);
        return baseMapper.update(null,luw) > 0;
    }

    @Override
    public Map<String, Object> addSysUser(SysUser sysUser) {
        Map<String,Object> map = new HashMap<>();
        if(null == sysUser){
            map.put("error","用户信息不能为空");
            return map;
        }
        // 内容缺失
        if(StringUtils.isBlank(sysUser.getUsername())){
            map.put("error","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(sysUser.getPassword())){
            map.put("error","密码不能为空");
            return map;
        }
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.select(SysUser::getId).eq(SysUser::getUsername,sysUser.getUsername());
        if(baseMapper.selectList(lqw).size()>0){
            map.put("error","用户名已存在！");
            return map;
        }
        // 设置Md5加密的密码
        sysUser.setPassword(CommonUtils.md5(sysUser.getPassword()));

        this.save(sysUser);

        return map;
    }

    @Override
    public Map<String, Object> updatePassword(UpdatePwdVo updatePwdVo) {
        Map<String,Object> map = new HashMap<>();
        String id = updatePwdVo.getId();
        if(StringUtils.isEmpty(id)){
            map.put("error","请选择用户！");
            return map;
        }
        if("1".equals(id)){
            map.put("error","不允许修改超级管理员！！");
            return map;
        }
        if(StringUtils.isEmpty(updatePwdVo.getOldPassword()) || StringUtils.isEmpty(updatePwdVo.getNewPassword()) ||
            StringUtils.isEmpty(updatePwdVo.getReNewPassowrd())){
            map.put("error", "密码不能为空！！");
            return map;
        }
        if(!updatePwdVo.getNewPassword().equals(updatePwdVo.getReNewPassowrd())){
            map.put("error","两次输入的新密码不一致，请检查！");
            return map;
        }
        SysUser user = getById(id);
        if(!CommonUtils.md5(updatePwdVo.getOldPassword()).equals(user.getPassword())){
            map.put("error","旧密码错误，请重新输入！");
            return map;
        }
        user.setPassword(CommonUtils.md5(updatePwdVo.getNewPassword()));
        baseMapper.updateById(user);
        return null;
    }

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isEmpty(loginVo.getUsername())){
            map.put("error","登录用户名不能为空！");
            return map;
        }
        if(StringUtils.isEmpty(loginVo.getPassword())){
            map.put("error","登录密码不能为空！");
            return map;
        }
        // 根据用户名获取用户信息
        SysUser sysUser = this.getUserInfoByUsername(loginVo.getUsername());
        if(sysUser == null){
            // 为空
            map.put("error","登录用户名不存在");
            return map;
        }
        // 判断用户是否可用
        if(sysUser.getStatus()==0){
            map.put("error","用户已停用，请飞书联系管理员！");
            return map;
        }
        // 判断密码是否一致
        String password =CommonUtils.md5(loginVo.getPassword());
        if(!password.equals(sysUser.getPassword())){
            map.put("error", "密码错误！");
            return map;
        }
        // 生成token
        val token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        map.put("token",token);
        return map;
    }

    @Override
    public SysUser getUserInfoByUsername(String username) {
        LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getUsername,username).eq(SysUser::getIsDeleted,0);
        return baseMapper.selectOne(lqw);
    }

    @Override
    public Map<String, Object> getUserInfo(String id,String username) {
        // 根据id查角色列表
        List<SysRole> sysRoleList = sysRoleService.getUserRoles(id);
        // 根据id查菜单和按钮权限
        Map<String,Object> res = sysMenuService.getUserMenuAndButtonList(id);

        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("roles",sysRoleList);
        //map.put("avatat","123");
        map.put("routers",res.get("routerVoList"));
        map.put("buttons",res.get("permsList"));
        return map;
    }

    @Override
    @DataScope(deptAlias = "d",userAlias = "u")
    public List<SysUser> getUsersByRoleId(SysRole sysRole) {
        return baseMapper.getUsersByRoleId(sysRole);
    }
}
