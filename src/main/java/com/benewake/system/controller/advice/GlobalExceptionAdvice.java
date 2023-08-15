package com.benewake.system.controller.advice;

import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author Lcs
 * @since 2023年08月01 16:13
 * 描 述： TODO
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 全局异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message(e.getMessage());
    }

    /**
     * 特点异常
     * @param e
     * @return
     */
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("运算有误！");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException{
        return Result.fail().code(ResultCodeEnum.PERMISSION.getCode()).message("没有当前功能权限");
    }
}
