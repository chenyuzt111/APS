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



    /**
     * 看当前用户是否是超级管理员
     */
    protected void handleColScope(JoinPoint point, ColScope controllerColScope) {//point是一个切点对象，controllerColScope是一个ColScope注解、作为方法参数传递进来获取列数据权限配置
        // 获取当前的用户
        SysUser currentUser = hostHolder.getUser();
        // 如果是超级管理员，则不过滤数据
        if (currentUser != null && !"1".equals(currentUser.getId()))
        {
            //执行colScopeFilter进行列数据权限过滤
            colScopeFilter(point, currentUser, controllerColScope.menuAlias());
        }
    }

    /**
     * 这段代码的主要目的是根据当前用户的角色和数据权限配置，确定哪些列是可见的，并将这些列信息拼接到 SQL 查询中，以实现列级别的数据权限控制。
     */

    //point是一个切点对象，包含了正在执行的方法和方法参数，currentUser是当前登陆的用户对象包含了相关信息，menuId是表示视图ID的参数，用于确定数据权限应用是那个菜单或视图
    protected void colScopeFilter(JoinPoint point,SysUser currentUser,String menuId){
        //resCol创建用于存储用户有权限查看的列
        Set<String> resCol = new HashSet<>();
        //遍历当前用户拥有的角色列表
        for(SysRole role : currentUser.getRoleList()){
            //根据当前角色和菜单ID获取角色在指定菜单下的列数据权限配置信息
            List<ApsCol> cols = apsColService.getColsByRoleAndMenu(role.getId(),menuId);
            if(cols.size()==0){
                // 未找到限制列 获取该菜单下的所有列，并将这些列加入到resCol集合里
                apsColService.getColsByMenuId(menuId).forEach(c->{
                    resCol.add(c.getColName());
                });
                break;
            }
            // 有限制列的话，将限制列加入集合
            cols.forEach(c->{
                resCol.add(c.getColName());
            });
        }
        // 将取出的可展示列拼接到sql中
        //首先将resCol集合转化为列表，以便于后续进行拼接操作
        List<String> res = new ArrayList<>(resCol);
        //获取方法中的参数
        Object params = point.getArgs()[0];
        //检查方法中的参数是否为空，或者参数是否是BaseEntity类型
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            //将方法参数强制转换为BaseEntity类型一边出读取其中的属性和方法
            BaseEntity baseEntity = (BaseEntity) params;
            //将可见列的名称拼接成字符串，使用逗号分隔存储在BaseEntity对象的参数中键名为COL_SCOPE
            baseEntity.getParam().put(COL_SCOPE, String.join(",",res));
        }
    }

    /**
     * 拼接权限sql前先清空params.colScope参数防止注入
     */
    private void clearColScope(final JoinPoint joinPoint)//joinPoint是一个切点对象
    {
        //获取方法的参数列表，取第一个参数，这个参数通常包含了方法参数的对象
        Object params = joinPoint.getArgs()[0];
        //要先判断参数对象是否是BaseEntity类的实例，这样才能确保清空操作只在特定类型的参数对象上进行
        if (params instanceof BaseEntity)
        {
            //若是的话强制转化为BaseEntity对象，以便于取数据
            BaseEntity baseEntity = (BaseEntity) params;
            //将参数获取为一个map，将COL_SCOPE键设为空字符串，以清空列数据权限过滤参数
            baseEntity.getParam().put(COL_SCOPE, "");
        }
    }
}
