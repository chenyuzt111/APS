package com.benewake.system.security.custom;

import com.benewake.system.utils.CommonUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Lcs
 * @since 2023年08月04 10:40
 * 描 述： TODO
 */
@Component
public class CustomMd5Password implements PasswordEncoder {
    public String encode(CharSequence rawPassword) {
        return CommonUtils.md5(rawPassword.toString());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println(rawPassword+" "+ CommonUtils.md5(rawPassword.toString())+" "+encodedPassword);
        return encodedPassword.equals(CommonUtils.md5(rawPassword.toString()));
    }
}
