package com.benewake.system.aspect;

import com.benewake.system.annotation.ColScope;
import com.benewake.system.entity.base.BaseEntity;
import com.benewake.system.entity.system.ApsCol;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.ApsColService;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.service.SysUserService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lcs
 * @since 2023年08月14 15:23
 * 描 述：
 * 对于添加ColScope注解的方法进行列权限过滤
 * 根据ColScope注解上的menuId和当前登录用户所有的角色id
 * 查询该角色在这个menu中可以展示的列字段，若找不到则表示该角色无列权限限制，展示所有字段
 *
 * 实 现：采用AOP及sql拼接的方式
 */
@Aspect
@Component
public class ColScopeAspect {

    /**
     * 列数据权限过滤关键字
     */
    public static final String COL_SCOPE = "colScope";
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private ApsColService apsColService;

    @Before("@annotation(controllerColScope)")
    public void doBefore(JoinPoint point, ColScope controllerColScope) throws Throwable
    {
        clearColScope(point);
        handleColScope(point, controllerColScope);
    }

    protected void handleColScope(JoinPoint point, ColScope controllerColScope) {
        // 获取当前的用户
        SysUser currentUser = hostHolder.getUser();
        // 如果是超级管理员，则不过滤数据
        if (currentUser != null && !"1".equals(currentUser.getId()))
        {
            colScopeFilter(point, currentUser, controllerColScope.menuAlias());
        }
    }

    protected void colScopeFilter(JoinPoint point,SysUser currentUser,String menuId){
        Set<String> resCol = new HashSet<>();
        for(SysRole role : currentUser.getRoleList()){
            List<ApsCol> cols = apsColService.getColsByRoleAndMenu(role.getId(),menuId);
            if(cols.size()==0){
                // 未找到限制列 表示该角色可以查看所有列信息
                apsColService.getColsByMenuId(menuId).forEach(c->{
                    resCol.add(c.getColName());
                });
                break;
            }
            // 加入集合
            cols.forEach(c->{
                resCol.add(c.getColName());
            });
        }
        // 将取出的可展示列拼接到sql中
        List<String> res = new ArrayList<>(resCol);
        Object params = point.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParam().put(COL_SCOPE, String.join(",",res));
        }
    }

    /**
     * 拼接权限sql前先清空params.colScope参数防止注入
     */
    private void clearColScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParam().put(COL_SCOPE, "");
        }
    }
}
