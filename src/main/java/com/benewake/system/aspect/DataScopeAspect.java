package com.benewake.system.aspect;

import com.benewake.system.annotation.DataScope;
import com.benewake.system.entity.base.BaseEntity;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.utils.BenewakeStringUtils;
import com.benewake.system.utils.HostHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据过滤处理
 * @author Lcs
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SysRoleService sysRoleService;

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 获取当前的用户
        SysUser currentUser = hostHolder.getUser();
        // 如果是超级管理员，则不过滤数据
        if (currentUser != null && !"1".equals(currentUser.getId()))
        {
            dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
                    controllerDataScope.userAlias());
        }

    }

    /**
     * 数据范围过滤
     * 多角色则展示最大权限  (使用OR相连)
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     */
    //joinPoint切点对象包含方法和方法参数，user当前用户对象，deptAlias部门别名，userAlias用户别名
    protected void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias)
    {
        //StringBuilder对象用于存储生成的sql片段
        StringBuilder sqlString = new StringBuilder();
        //遍历当前用户拥有的角色列表
        for (SysRole role : user.getRoleList())
        {
            //dataScope表示数据权限的类型
            String dataScope = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(dataScope))
            {
                // 如果获取到dataScope是1，说明可以获得全部权限，清除sqlString
                sqlString = new StringBuilder();
                break;
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                // 自定义权限
                sqlString.append(BenewakeStringUtils.format(
                        " OR {}.id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                        role.getId()));
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope))
            {
                // 部门权限
                sqlString.append(BenewakeStringUtils.format(" OR {}.id = {} ", deptAlias, user.getDeptId()));
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                // 部门及以下权限
                sqlString.append(BenewakeStringUtils.format(
                        " OR {}.id IN ( SELECT id FROM sys_dept WHERE id = {} or find_in_set( {} , ancestors ) )",
                        deptAlias, user.getDeptId(), user.getDeptId()));
            }
            else if (DATA_SCOPE_SELF.equals(dataScope))
            {
                // 仅本人权限
                if (BenewakeStringUtils.isNotBlank(userAlias))
                {
                    sqlString.append(BenewakeStringUtils.format(" OR {}.id = {} ", userAlias, user.getId()));
                }
                else
                {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(" OR 1=0 ");
                }
            }
        }

        //如果sqlString不为空的话说明有数据权限需要配置添加到sql查询中
        if (BenewakeStringUtils.isNotBlank(sqlString.toString()))
        {

            Object params = joinPoint.getArgs()[0];
            if (BenewakeStringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                //切点对象转化为baseEntity对象
                BaseEntity baseEntity = (BaseEntity) params;
                //将这些sql片段加入到DATA_SCOPE对象的参数中键名为DATA_SCOPE
                baseEntity.getParam().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParam().put(DATA_SCOPE, "");
        }
    }
}
