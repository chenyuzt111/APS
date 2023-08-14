package com.benewake.system.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author Lcs
 * 描述：一些通用工具
 */
public class CommonUtils {
    // MD5加密用的盐
    private static final String SALT = "benewake.com";
    public static void main(String[] args) {
        String password = md5("admin123");
        System.out.println(password);
    }



    // 生成随机字符串UUID
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5 加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        key += SALT;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}
