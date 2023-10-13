package com.benewake.system.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lcs
 * @since 2023年08月03 14:47
 * 描 述： TODO
 */
@Data
public class UpdatePwdVo implements Serializable {
    private static final long serialVersionUID = 1L;
//    private String id;
    private String oldPassword;
    private String newPassword;
    private String reNewPassowrd;

}
