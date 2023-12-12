package com.benewake.system.service.scheduling.mes.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsSnLabeling;
import com.benewake.system.entity.dto.ApsSnLabelingDto;
import com.benewake.system.entity.mes.MesSnLabeling;
import com.benewake.system.mapper.ApsSnLabelingMapper;
import com.benewake.system.service.scheduling.mes.ApsSnLabelingService;
import com.benewake.system.transfer.MesToApsSnLabeling;
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
 * @description 针对表【aps_sn_labeling】的数据库操作Service实现
 * @createDate 2023-10-19 13:50:27
 */
@Service
public class ApsSnLabelingServiceImpl extends ServiceImpl<ApsSnLabelingMapper, ApsSnLabeling>
        implements ApsSnLabelingService {

    @Autowired
    private MesToApsSnLabeling mesToApsSnLabeling;

    @Autowired
    private ApsSnLabelingMapper snLabelingMapper;

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = this.getMaxVersionIncr();
        String baseUrl = "http://ql.benewake.com//openApi"; // 替换为实际的API URL

        String app = "c51847c9";
        String requestBody = "{\"pageSize\": 1000, \"pageNum\": 1}";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/app/" + app + "/apply/filter");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accessToken", accessToken);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            ArrayList<ApsSnLabeling> apsSnLabelings = null;
            if (response.getEntity() != null) {
                String responseString = EntityUtils.toString(response.getEntity());
                Map<String, String> fieldMapping = new HashMap<>();
                fieldMapping.put("生产订单编号", "productionOrderNumber");
                fieldMapping.put("本次粘贴完成数", "burnInCompletionQuantity");
                fieldMapping.put("粘贴合格数", "BurnQualifiedCount");
                fieldMapping.put("粘贴不合格数", "UnBurnQualifiedCount");
                fieldMapping.put("物料编码 ", "materialCode");
                fieldMapping.put("物料名称", "materialName");
                fieldMapping.put("订单总数", "totalNumber");
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                JsonArray answersArray = jsonObject.getAsJsonObject("result").getAsJsonArray("result");
                List<MesSnLabeling> dataList = new ArrayList<>();
                for (JsonElement element : answersArray) {
                    JsonObject answer = element.getAsJsonObject();
                    JsonArray answerArray = answer.getAsJsonArray("answers"); // Replace "your_array_key" with the actual key
                    MesSnLabeling snLabeling = new MesSnLabeling();
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
                                    Field field = snLabeling.getClass().getDeclaredField(excelFieldName);
                                    field.setAccessible(true);
                                    if (excelFieldName.equals("BurnQualifiedCount")) {
                                        if (id == 84935594) {
                                            field.set(snLabeling, dataValue);
                                        }
                                    }else if (excelFieldName.equals("UnBurnQualifiedCount")){
                                        if (id == 84935542) {
                                            field.set(snLabeling, dataValue);
                                        }
                                    } else {
                                        field.set(snLabeling, dataValue);
                                    }
                                    field.setAccessible(false);
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    dataList.add(snLabeling);
                    apsSnLabelings = new ArrayList<>();
                    for (MesSnLabeling labeling : dataList) {
                        ApsSnLabeling apsSnLabeling = mesToApsSnLabeling.convert(labeling, maxVersionIncr);
                        apsSnLabelings.add(apsSnLabeling);
                    }
                }
            }
            return saveBatch(apsSnLabelings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsSnLabelingDto> snLabelingDtoPage = snLabelingMapper.selectPageList(page, tableVersionList);
        return snLabelingDtoPage;
    }

    @Override
    public void insertVersionIncr() {
        snLabelingMapper.insertVersionIncr();
    }
}




