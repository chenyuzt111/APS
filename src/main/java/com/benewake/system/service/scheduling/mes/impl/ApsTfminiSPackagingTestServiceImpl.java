package com.benewake.system.service.scheduling.mes.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsTfminiSPackagingTest;
import com.benewake.system.entity.mes.MesPackagingTest;
import com.benewake.system.service.scheduling.mes.ApsTfminiSPackagingTestService;
import com.benewake.system.mapper.ApsTfminiSPackagingTestMapper;
import com.benewake.system.transfer.MesToApsTfminiSPackagingTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_packaging_test】的数据库操作Service实现
* @createDate 2023-10-19 14:52:15
*/
@Service
public class ApsTfminiSPackagingTestServiceImpl extends ServiceImpl<ApsTfminiSPackagingTestMapper, ApsTfminiSPackagingTest>
    implements ApsTfminiSPackagingTestService {

    @Autowired
    private MesToApsTfminiSPackagingTest mesToApsTfminiSPackagingTest;
    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = this.getMaxVersionIncr();
        String baseUrl = "http://ql.benewake.com//openApi"; // 替换为实际的API URL

        String app = "0a3352f6";
        String requestBody = "{\"pageSize\": 1000, \"pageNum\": 1}";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/app/" + app + "/apply/filter");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accessToken", accessToken);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            ArrayList<ApsTfminiSPackagingTest> apsTfminiSPackagingTests = null;
            if (response.getEntity() != null) {
                String responseString = EntityUtils.toString(response.getEntity());
                Map<String, String> fieldMapping = new HashMap<>();
                fieldMapping.put("生产订单编号", "productionOrderNumber");
                fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
                fieldMapping.put("包装终检合格数", "BurnQualifiedCount");
                fieldMapping.put("物料编码", "materialCode");
                fieldMapping.put("物料名称", "materialName");
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                JsonArray answersArray = jsonObject.getAsJsonObject("result").getAsJsonArray("result");
                List<MesPackagingTest> dataList = new ArrayList<>();
                for (JsonElement element : answersArray) {
                    JsonObject answer = element.getAsJsonObject();
                    JsonArray answerArray = answer.getAsJsonArray("answers"); // Replace "your_array_key" with the actual key
                    MesPackagingTest baozhuang = new MesPackagingTest();
                    for (int i = 0; i < answerArray.size(); i++) {
                        JsonObject item = answerArray.get(i).getAsJsonObject();
                        String queTitle = item.get("queTitle").getAsString();
                        if (fieldMapping.containsKey(queTitle)) {
                            String excelFieldName = fieldMapping.get(queTitle);
                            JsonArray valuesArray = item.getAsJsonArray("values");
                            for (int j = 0; j < valuesArray.size(); j++) {
                                JsonObject value = valuesArray.get(j).getAsJsonObject();
                                String dataValue = value.get("dataValue").getAsString();
                                int id = Integer.parseInt(value.get("queId").getAsString());
                                try {
                                    Field field = baozhuang.getClass().getDeclaredField(excelFieldName);
                                    field.setAccessible(true);
                                    if (excelFieldName.equals("BurnQualifiedCount")) {
                                        if (id == 84940223) {
                                            field.set(baozhuang, dataValue);
                                        }
                                    } else {
                                        field.set(baozhuang, dataValue);
                                    }
                                    field.setAccessible(false);
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    dataList.add(baozhuang);
                    apsTfminiSPackagingTests = new ArrayList<>();
                    for (MesPackagingTest mesPackagingTest : dataList) {
                        ApsTfminiSPackagingTest apsTfminiSPackagingTest = mesToApsTfminiSPackagingTest.convert(mesPackagingTest, maxVersionIncr);
                        apsTfminiSPackagingTests.add(apsTfminiSPackagingTest);
                    }
                }
            }
            return saveBatch(apsTfminiSPackagingTests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return null;
    }
}




