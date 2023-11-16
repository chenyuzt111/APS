package com.benewake.system.service.scheduling.mes.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsTfminiSInstallationBoard;
import com.benewake.system.entity.mes.MesInstallationBoard;
import com.benewake.system.service.scheduling.mes.ApsTfminiSInstallationBoardService;
import com.benewake.system.mapper.ApsTfminiSInstallationBoardMapper;
import com.benewake.system.transfer.MesToApsTfminiSInstallationBoard;
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
* @description 针对表【aps_tfmini_s_installation_board】的数据库操作Service实现
* @createDate 2023-10-19 11:44:00
*/
@Service
public class ApsTfminiSInstallationBoardServiceImpl extends ServiceImpl<ApsTfminiSInstallationBoardMapper, ApsTfminiSInstallationBoard>
    implements ApsTfminiSInstallationBoardService{

    @Autowired
    private MesToApsTfminiSInstallationBoard mesToApsTfminiSInstallationBoard;

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = this.getMaxVersionIncr();
        String baseUrl = "http://ql.benewake.com//openApi"; // 替换为实际的API URL

        String app = "057d7fe7";
        String requestBody = "{\"pageSize\": 10000, \"pageNum\": 1}";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/app/" + app + "/apply/filter");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accessToken", accessToken);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            ArrayList<ApsTfminiSInstallationBoard> apsTfminiSInstallationBoards = null;
            if (response.getEntity() != null) {
                String responseString = EntityUtils.toString(response.getEntity());
                Map<String, String> fieldMapping = new HashMap<>();
                fieldMapping.put("生产订单编号", "productionOrderNumber");
                fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
                fieldMapping.put("安装合格数", "BurnQualifiedCount");
                fieldMapping.put("安装不合格数", "UnBurnQualifiedCount");
                fieldMapping.put("物料编码", "materialCode");
                fieldMapping.put("物料名称", "materialName");
                fieldMapping.put("订单总数", "totalNumber");
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                JsonArray answersArray = jsonObject.getAsJsonObject("result").getAsJsonArray("result");
                List<MesInstallationBoard> dataList = new ArrayList<>();
                for (JsonElement element : answersArray) {
                    JsonObject answer = element.getAsJsonObject();
                    JsonArray answerArray = answer.getAsJsonArray("answers"); // Replace "your_array_key" with the actual key
                    MesInstallationBoard MesInstallationBoard = new MesInstallationBoard();
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
                                    Field field = MesInstallationBoard.getClass().getDeclaredField(excelFieldName);
                                    field.setAccessible(true);
                                    if (excelFieldName.equals("BurnQualifiedCount")) {
                                        if (id == 84939594) {
                                            field.set(MesInstallationBoard, dataValue);
                                        }
                                    }else if (excelFieldName.equals("UnBurnQualifiedCount")){
                                        if (id == 84939593) {
                                            field.set(MesInstallationBoard, dataValue);
                                        }
                                    } else {
                                        field.set(MesInstallationBoard, dataValue);
                                    }
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    dataList.add(MesInstallationBoard);
                    apsTfminiSInstallationBoards = new ArrayList<>();
                    for (MesInstallationBoard mesInstallationBoard : dataList) {
                        ApsTfminiSInstallationBoard apsTfminiSInstallationBoard = mesToApsTfminiSInstallationBoard.convert(mesInstallationBoard, maxVersionIncr);
                        apsTfminiSInstallationBoards.add(apsTfminiSInstallationBoard);
                    }
                }
            }
            return saveBatch(apsTfminiSInstallationBoards);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return null;
    }
}




