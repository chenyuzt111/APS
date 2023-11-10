package com.benewake.system.mes;

import com.benewake.system.entity.mes.MesInstallationBoard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MesInstallationBoardzhuban {
    public static void main(String[] args) {
        String baseUrl = "http://ql.benewake.com//openApi"; // 替换为实际的API URL
        String accessToken = "2633eb1b-1348-4a8e-bda2-e66540e1d8b7"; // 替换为实际的accessToken
        String app = "949664af";
        // 创建请求体 JSON 字符串
        String requestBody = "{\"pageSize\": 10000, \"pageNum\": 1}";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/app/" + app + "/apply/filter");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accessToken", accessToken);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getEntity() != null) {
                String responseString = EntityUtils.toString(response.getEntity());
                // 声明并初始化 queTitle 和 @ExcelProperty 之间的映射
                Map<String, String> fieldMapping = new HashMap<>();
                fieldMapping.put("生产订单编号", "productionOrderNumber");
                fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
                fieldMapping.put("安装合格数", "BurnQualifiedCount");
                fieldMapping.put("物料编码", "materialCode");
                fieldMapping.put("物料名称", "materialName");
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                JsonArray answersArray = jsonObject.getAsJsonObject("result").getAsJsonArray("result");
                List<MesInstallationBoard> dataList = new ArrayList<>();
                for (JsonElement element : answersArray) {
                    JsonObject answer = element.getAsJsonObject();
                    JsonArray answerArray = answer.getAsJsonArray("answers"); // Replace "your_array_key" with the actual key
                    MesInstallationBoard MesInstallationBoard = new MesInstallationBoard();
                    for (int i = 0; i < answerArray.size(); i++) {
                        JsonObject item = answerArray.get(i).getAsJsonObject();
                        // 获取queTitle
                        String queTitle = item.get("queTitle").getAsString();
                        if (fieldMapping.containsKey(queTitle)) {
                            String excelFieldName = fieldMapping.get(queTitle);
                            JsonArray valuesArray = item.getAsJsonArray("values");
                            for (int j = 0; j < valuesArray.size(); j++) {
                                JsonObject value = valuesArray.get(j).getAsJsonObject();
                                String dataValue = value.get("dataValue").getAsString();
                                int id= Integer.parseInt(value.get("queId").getAsString());
                                try {
                                    Field field = MesInstallationBoard.getClass().getDeclaredField(excelFieldName);
                                    field.setAccessible(true);
                                    // 如果字段是BurnQualifiedCount并且还没有设置过，设置字段
                                    if (excelFieldName.equals("BurnQualifiedCount")){
                                        if(id==84934591){
                                            field.set(MesInstallationBoard, dataValue);
                                        }
                                    }else{
                                        field.set(MesInstallationBoard, dataValue);
                                    }
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    // 处理反射异常，如字段不存在或访问权限问题
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    // 将pcshaolu对象添加到数据列表
                    dataList.add(MesInstallationBoard);
                    for (MesInstallationBoard mesInstallationBoard : dataList) {
                        System.out.println(mesInstallationBoard.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
