package com.benewake.system.KindDee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lcs
 * 描 述：一些通用常量定义
 */
public interface BenewakeConstants {
    /**
     * 金蝶授权信息
     */
//    String X_KDAPI_ACCTID = "20210222142620903";
//    String X_KDAPI_USERNAME = "王丽";
//    String X_KDAPI_APPID = "257724_xc5P7wlv0kk84bUp116DQz0qTI5Z6OqI";
//    String X_KDAPI_APPSEC = "b6f22a40a0764f89a5654016e89230e8";
//    String X_KDAPI_SERVICEURL = "https://benewake.test.ik3cloud.com/k3cloud/";
//    int X_KDAPI_LCID = 2052;

    String X_KDAPI_ACCTID = "20210222141008331";
    String X_KDAPI_USERNAME = "FIM+APS系统集成账户";
    String X_KDAPI_APPID = "258135_06eqT6GoTuq9RfSL726N7dzGzJ2/5Cms";
    String X_KDAPI_APPSEC = "85cb5f3d3b5e4104b3d3dd6c69c61b4b";
    String X_KDAPI_SERVICEURL = "https://benewake.test.ik3cloud.com/k3cloud/";
//  String X_KDAPI_SERVICEURL = "https://benewake.test.ik3cloud.com/k3cloud/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc";

    int X_KDAPI_LCID = 2052;

    /**
     * 一天的时间
     */
    int ONE_DAY = 1000*60*60*24;
    /**
     * 响应code：成功
     */
    Integer SUCCESS_CODE = 200;
    /**
     * 响应code：失败
     */
    Integer FAIL_CODE = 400;
    /**
     * 响应message：成功
     */
    String SUCCESS_MESSAGE = "success";
    /**
     * 响应message：失败
     */
    String FAIL_MESSAGE = "fail";
    /**
     * 默认状态的登录凭证超时时间 (7天)
     */
    int DEFAULT_EXPIRED_SECONDS = 3600*24*7;
    /**
     * 用户类型：管理员
     */
    Long USER_TYPE_ADMIN = 1L;
    /**
     * 用户类型：销售员
     */
    Long USER_TYPE_SALESMAN = 2L;
    /**
     * 用户类型：访客
     */
    Long USER_TYPE_VISITOR = 3L;
    /**
     * 用户类型：无效
     */
    Long USER_TYPE_INVALID = 4L;
    /**
     * 用户类型：系统
     */
    Long USER_TYPE_SYSTEM = 5L;

    /**
     * 筛选条件
     */
    String LIKE = "like";
    String NOT_LIKE = "notlike";
    String GREATER = "gt";
    String GREATER_OR_EQUAL = "ge";
    String EQUAL = "eq";
    String NOT_EQUAL = "ne";
    String LITTER = "lt";
    String LITTER_OR_EQUAL = "le";
    String IS_NULL = "null";
    String NOT_NULL = "notnull";

    /**
     * 订单类型：销售预测
     */
    int ORDER_TYPE_YC = 4;
    /**
     * 订单类型：询单
     */
    int ORDER_TYPE_XD = 5;
    /**
     * 订单类型：用户付款
     */
    int ORDER_TYPE_PO = 1;
    /**
     * 订单类型：用户付款意向
     */
    int ORDER_TYPE_PR = 2;
    /**
     * 订单类型：供应链预估
     */
    int ORDER_TYPE_YG = 3;

    /**
     * 产品类型：已有竞品
     */
    int ITEM_TYPE_ALREADY_AVAILABLE = 1;
    /**
     * 产品类型：已有定制
     */
    int ITEM_TYPE_ALREADY_BESPOKE = 2;
    /**
     * 产品类型：软件定制
     */
    int ITEM_TYPE_SOFTWARE_BESPOKE = 3;
    /**
     * 产品类型：原材料定制
     */
    int ITEM_TYPE_RAW_MATERIALS_BESPOKE = 4;
    /**
     * 产品类型：原材料+软件定制
     */
    int ITEM_TYPE_MATERIALS_AND_SOFTWARE_BESPOKE = 5;


    /**
     * 初始订单类型：销售询单
     */
    int INQUIRY_INIT_TYPE_XD = 5;
    /**
     * 初始订单类型：销售预测
     */
    int INQUIRY_INIT_TYPE_YC = 4;
    /**
     * 初始订单类型：供应链预估
     */
    int INQUIRY_INT_TYPE_YG = 3;


    /**
     * 表名：全部订单列表
     */
    Long ALL_TABLE = 1L;
    /**
     * 表名：订单状态
     */
    Long INQUIRY_TYPE_TABLE = 2L;
    /**
     * 表名：客户类型
     */
    Long CUSTOMER_TYPE_TABLE = 3L;
    /**
     * 表名：产品类型
     */
    Long ITEM_TYPE_TABLE = 4L;
    /**
     * 表名：订单转换
     */
    Long INQUIRY_CHANGE_TABLE = 5L;
    /**
     * 表名：订单交付进度
     */
    Long INQUIRY_DELIVERY_TABLE = 6L;



}
