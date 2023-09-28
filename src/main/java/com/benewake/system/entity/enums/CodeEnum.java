package com.benewake.system.entity.enums;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public enum CodeEnum {
    SUCCESS("SUCCESS", "调用成功"),
    SYSTEM_ERROR("FAIL_BIZ_GLOBAL_SYSTEM_ERROR", "系统错误"),
    APPLICATION_ERROR("FAIL_BIZ_GLOBAL_APPLICATION_ERROR", "应用错误"),
    RPC_ERROR("FAIL_BIZ_GLOBAL_RPC_ERROR", "第三方调用错误"),
    TIME_OUT_ERROR("FAIL_BIZ_GLOBAL_TIME_OUT_ERROR", "网络超时"),
    FLOW_LIMITING("BLOCKED_BY_FLOW_LIMITING", "请求过于频繁，限制访问"),
    APP_ACTION_ERROR("FAIL_BIZ_GLOBAL_APP_ACTION_ERROR", "应用错误, 可控制客户端行为"),
    WRONG_PARAMS("FAIL_BIZ_GLOBAL_WRONG_PARAMS", "参数解析错误"),
    INTERFACE_ERROR("FAIL_BIZ_GLOBAL_INTERFACE_ERROR", "接口调用错误"),
    NEED_LOGIN("FAIL_BIZ_GLOBAL_NEED_LOGIN", "未登录"),
    INVALID_PARAMS("FAIL_BIZ_GLOBAL_INVALID_PARAMS", "参数异常"),
    NOT_FOUND("FAIL_BIZ_GLOBAL_NOT_FOUND", "资源不存在"),
    ANTI_BRUSH("FAIL_BIZ_GLOBAL_ANTI_BRUSH", "哎呀呀,慢一点");


    private String msg;
    private String code;

    private CodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getCode() {
        return this.code;
    }

}
