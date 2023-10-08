package com.benewake.system.KindDee;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.json.JSONObject;

public class Program {

    static String X_KDAPI_ACCTID = "20210222141008331";
    String X_KDAPI_USERNAME = "FIM+APS系统集成账户";
    String X_KDAPI_APPID = "258135_06eqT6GoTuq9RfSL726N7dzGzJ2/5Cms";
    String X_KDAPI_APPSEC = "85cb5f3d3b5e4104b3d3dd6c69c61b4b";
    String X_KDAPI_SERVICEURL = "https://benewake.test.ik3cloud.com/k3cloud/";
    int X_KDAPI_LCID = 2052;

    public static void main(String[] args) {
//        http://{服务器IP或域名}/k3cloud/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc
        String baseUrl = "https://benewake.ik3cloud.com/K3Cloud"; // 服务器地址
        HttpClientEx httpClient = new HttpClientEx();
        httpClient.setUrl(String.format("%s/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc", baseUrl));

        List<Object> parameters = new ArrayList<>();
        parameters.add("20210222141008331"); // 帐套Id
        parameters.add("FIM+APS系统集成账户"); // 用户名
        parameters.add("85cb5f3d3b5e4104b3d3dd6c69c61b4b"); // 密码
        parameters.add(2052); // 多语言：中文

        httpClient.setContent(new JSONObject(parameters).toString());

        JSONObject result = new JSONObject(httpClient.asyncRequest());

        int iResult = result.getInt("LoginResultType");

        if (iResult == 1) {
            while (true) {
                System.out.println("请输入任意字符回车继续");
                String key = System.console().readLine();

                if (key.equals("exit")) {
                    break;
                }

                httpClient.setUrl(String.format("%s/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc", baseUrl));

                parameters = new ArrayList<>();
                InventoryParamModel model = new InventoryParamModel();
                model.setFstocknumbers("CK001,CK002,CK003");
                model.setIsshowauxprop(true);
                model.setIsshowstockloc(true);
                model.setPageindex(1);
                model.setPagerows(2);
                parameters.add(model);

                httpClient.setContent(new JSONObject(parameters).toString());

                String response = httpClient.asyncRequest();
                System.out.println(String.format("结果=%s", response));
            }
        }
    }
}

@Data
class InventoryParamModel {

    private String fstockorgnumbers;
    private String fmaterialnumbers;
    private String fstocknumbers;
    private String flotnumbers;
    private boolean isshowstockloc;
    private boolean isshowauxprop;
    private int pageindex;
    private int pagerows;

    // 在这里添加 getters 和 setters
}

class HttpClientEx {

    private String url;
    private String content;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String asyncRequest() {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(1000 * 60 * 10); // 10分钟超时

            JSONObject jObj = new JSONObject();
            jObj.put("format", 1);
            jObj.put("useragent", "ApiClient");
            jObj.put("rid", Integer.toString(Math.abs(java.util.UUID.randomUUID().hashCode())));
            jObj.put("parameters", this.content);
            jObj.put("timestamp", 1694678368);
            jObj.put("v", "1.0");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] bytes = jObj.toString().getBytes("UTF-8");
                os.write(bytes);
                os.flush();
            }

            try (InputStream is = connection.getInputStream()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    return validateResult(reader.readLine());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "请求出错：" + e.getMessage();
        }
    }

    private String validateResult(String responseText) {
        if (responseText.startsWith("response_error:")) {
            return responseText.replace("response_error:", "").trim();
        }
        return responseText;
    }
}
