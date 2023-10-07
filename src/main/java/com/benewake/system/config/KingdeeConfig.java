package com.benewake.system.config;

import com.benewake.system.utils.BenewakeConstants;
import com.kingdee.bos.webapi.entity.IdentifyInfo;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KingdeeConfig implements BenewakeConstants {

    @Bean
    public K3CloudApi k3CloudApi() {
        IdentifyInfo iden = new IdentifyInfo();
        iden.setUserName(X_KDAPI_USERNAME);
        iden.setAppId(X_KDAPI_APPID);
        iden.setdCID(X_KDAPI_ACCTID);
        iden.setAppSecret(X_KDAPI_APPSEC);
        iden.setlCID(X_KDAPI_LCID);
        iden.setServerUrl(X_KDAPI_SERVICEURL);
        iden.setOrgNum("100");
        K3CloudApi api = new K3CloudApi(iden);
        return api;
    }
}