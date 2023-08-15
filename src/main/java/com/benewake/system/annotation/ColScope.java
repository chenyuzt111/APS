package com.benewake.system.annotation;

import java.lang.annotation.*;

/**
 * @author lcs
 * @since 2023年08月14 15:20
 * 描 述：列权限注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColScope {
    /**
     * 视图（菜单）id
     */
    public String menuAlias() default "";

}
