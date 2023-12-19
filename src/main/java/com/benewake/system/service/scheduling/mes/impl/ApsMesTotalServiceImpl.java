package com.benewake.system.service.scheduling.mes.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMesTotal;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.mes.MesTotal;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.scheduling.mes.ApsMesTotalService;
import com.benewake.system.transfer.MesToApsMesTotal;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.benewake.system.mapper.ApsMesTotalMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author DELL
 * @description 针对表【aps_mes_total】的数据库操作Service实现
 * @createDate 2023-12-11 13:52:23
 */
@Service
@Component
public class ApsMesTotalServiceImpl extends ServiceImpl<ApsMesTotalMapper, ApsMesTotal>
        implements ApsMesTotalService {

    @Autowired
    private MesToApsMesTotal mesToApsMesTotal;

    @Autowired
    private ApsMesTotalMapper mesTotalMapper;


    private List<ApsMesTotal> apsMesTotals = null;

    private Integer maxVersionIncr;
    

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        return mesTotalMapper.selectPageList(page, tableVersionList);
    }

    @Override
    public void insertVersionIncr() {
        mesTotalMapper.insertVersionIncr();
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        return mesTotalMapper.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return mesTotalMapper.searchLike(versionToChVersionArrayList, queryWrapper);
    }

    private List<ApsMesTotal> updateDataVersions(String app, Map<String, String> fieldmapping, int qualifiedCountId, int unqualifiedCountId) throws Exception {
        String baseUrl = "http://ql.benewake.com//openApi";
        // 创建请求体 JSON 字符串
        String requestBody = "{\"pageSize\": 10000, \"pageNum\": 1}";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/app/" + app + "/apply/filter");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accessToken", accessToken);
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            ArrayList<ApsMesTotal> apsMesTotals = null;
            if (response.getEntity() != null) {
                String responseString = EntityUtils.toString(response.getEntity());
                Map<String, String> fieldMapping = new HashMap<>();
                fieldMapping = fieldmapping;

                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                JsonArray answersArray = jsonObject.getAsJsonObject("result").getAsJsonArray("result");
                List<MesTotal> dataList = new ArrayList<>();
                for (JsonElement element : answersArray) {
                    JsonObject answer = element.getAsJsonObject();
                    JsonArray answerArray = answer.getAsJsonArray("answers"); // Replace "your_array_key" with the actual key
                    MesTotal MesTotal = new MesTotal();
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
                                int id = Integer.parseInt(value.get("queId").getAsString());
                                try {
                                    Field field = MesTotal.getClass().getDeclaredField(excelFieldName);
                                    field.setAccessible(true);
                                    // 如果字段是burnQualifiedCount并且还没有设置过，设置字段
                                    if (excelFieldName.equals("burnQualifiedCount")) {
                                        if (id == qualifiedCountId) {
                                            field.set(MesTotal, dataValue);
                                        }
                                    } else if (excelFieldName.equals("unBurnQualifiedCount")) {
                                        if (id == unqualifiedCountId) {
                                            field.set(MesTotal, dataValue);
                                        }
                                    } else {
                                        field.set(MesTotal, dataValue);
                                    }
                                    field.setAccessible(false);
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    // 处理反射异常，如字段不存在或访问权限问题
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    // 将pcshaolu对象添加到数据列表
                    dataList.add(MesTotal);
                    apsMesTotals = new ArrayList<>();
                    for (MesTotal mesTotal : dataList) {
                        ApsMesTotal apsMesTotal = mesToApsMesTotal.convert(mesTotal, maxVersionIncr);
                        apsMesTotals.add(apsMesTotal);
                    }
                }
            }
            return apsMesTotals;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void LunaBurn() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次烧录完成数", "burnInCompletionQuantity");
        fieldMapping.put("烧录合格数", "burnQualifiedCount");
        fieldMapping.put("烧录不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("烧录工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> burn = updateDataVersions("63df52b8", fieldMapping, 84934807, 84934806);
        for (ApsMesTotal apsMesTotal : burn) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("主板程序烧录");
        }
        apsMesTotals.addAll(burn);
    }

    public void LunaVersion() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次分板完成数", "burnInCompletionQuantity");
        fieldMapping.put("分板合格数", "burnQualifiedCount");
        fieldMapping.put("分板不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("分板治具编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> version = updateDataVersions("e51262a3", fieldMapping, 84935179, 84935178);
        for (ApsMesTotal apsMesTotal : version) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("主板分板");
        }
        apsMesTotals.addAll(version);
    }

    public void LunaInstallationBoard() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
        fieldMapping.put("安装合格数", "burnQualifiedCount");
        fieldMapping.put("安装不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> installationBoard = updateDataVersions("949664af", fieldMapping, 84934591, 84934590);
        for (ApsMesTotal apsMesTotal : installationBoard) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("主板安装");
        }
        apsMesTotals.addAll(installationBoard);
    }

    public void LunaFixed() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
        fieldMapping.put("安装合格数", "burnQualifiedCount");
        fieldMapping.put("安装不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> fixed = updateDataVersions("bb6daa5b", fieldMapping, 84950527, 84950526);
        for (ApsMesTotal apsMesTotal : fixed) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("主板固定");
        }
        apsMesTotals.addAll(fixed);
    }

    public void LunaSnLabeling() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次粘贴完成数", "burnInCompletionQuantity");
        fieldMapping.put("粘贴合格数", "burnQualifiedCount");
        fieldMapping.put("粘贴不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> snlabeling = updateDataVersions("c51847c9", fieldMapping, 84935594, 84935542);
        for (ApsMesTotal apsMesTotal : snlabeling) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("粘贴SN标签");
        }
        apsMesTotals.addAll(snlabeling);
    }

    public void LunaCalibration() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次校准测试完成数", "burnInCompletionQuantity");
        fieldMapping.put("校准合格数", "burnQualifiedCount");
        fieldMapping.put("校准不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("测试工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> calibration = updateDataVersions("5dfb9e96", fieldMapping, 84935387, 84935386);
        for (ApsMesTotal apsMesTotal : calibration) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("校准测试");
        }
        apsMesTotals.addAll(calibration);
    }

    public void LunaPackagingTest() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
        fieldMapping.put("包装终检合格数", "burnQualifiedCount");
        fieldMapping.put("包装终检不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> packaging = updateDataVersions("a5cd51b0", fieldMapping, 84935951, 84935950);
        for (ApsMesTotal apsMesTotal : packaging) {
            apsMesTotal.setWorkpiece("tf-luna");
            apsMesTotal.setProcess("包装");
        }
        apsMesTotals.addAll(packaging);
    }

    public void SBurn() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次烧录完成数", "burnInCompletionQuantity");
        fieldMapping.put("烧录合格数", "burnQualifiedCount");
        fieldMapping.put("烧录不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("烧录工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> burn = updateDataVersions("7a174007", fieldMapping, 84939165, 84939164);
        for (ApsMesTotal apsMesTotal : burn) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("主板程序烧录");
        }
        apsMesTotals.addAll(burn);
    }

    public void SVersion() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次分板完成数", "burnInCompletionQuantity");
        fieldMapping.put("分板合格数", "burnQualifiedCount");
        fieldMapping.put("分板不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("分板治具编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> version = updateDataVersions("5d052c6c", fieldMapping, 84939374, 84939373);
        for (ApsMesTotal apsMesTotal : version) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("主板分板");
        }
        apsMesTotals.addAll(version);
    }

    public void SInstallationBoard() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
        fieldMapping.put("包装终检合格数", "burnQualifiedCount");
        fieldMapping.put("包装终检不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> installationBoard = updateDataVersions("057d7fe7", fieldMapping, 84939594, 84939593);
        for (ApsMesTotal apsMesTotal : installationBoard) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("主板安装");
        }
        apsMesTotals.addAll(installationBoard);
    }

    public void SFixed() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
        fieldMapping.put("安装合格数", "burnQualifiedCount");
        fieldMapping.put("安装不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> fixed = updateDataVersions("530edc56", fieldMapping, 84950991, 84950990);
        for (ApsMesTotal apsMesTotal : fixed) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("主板固定");
        }
        apsMesTotals.addAll(fixed);
    }

    public void SSnLabeling() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次粘贴完成数", "burnInCompletionQuantity");
        fieldMapping.put("粘贴合格数", "burnQualifiedCount");
        fieldMapping.put("粘贴不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> snlabeling = updateDataVersions("180a18b0", fieldMapping, 84939793, 84939792);
        for (ApsMesTotal apsMesTotal : snlabeling) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("粘贴SN标签");
        }
        apsMesTotals.addAll(snlabeling);
    }

    public void SCalibration() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次校准测试完成数", "burnInCompletionQuantity");
        fieldMapping.put("校准合格数", "burnQualifiedCount");
        fieldMapping.put("校准不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("测试工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> calibration = updateDataVersions("3552e2a8", fieldMapping, 84939999, 84939998);
        for (ApsMesTotal apsMesTotal : calibration) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("校准测试");
        }
        apsMesTotals.addAll(calibration);
    }

    public void SPackagingTest() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
        fieldMapping.put("包装终检合格数", "burnQualifiedCount");
        fieldMapping.put("包装终检不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> packaging = updateDataVersions("0a3352f6", fieldMapping, 84940223, 84940222);
        for (ApsMesTotal apsMesTotal : packaging) {
            apsMesTotal.setWorkpiece("tfmini-s");
            apsMesTotal.setProcess("包装");
        }
        apsMesTotals.addAll(packaging);
    }

    public void PlusBurn() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次烧录完成数", "burnInCompletionQuantity");
        fieldMapping.put("烧录合格数", "burnQualifiedCount");
        fieldMapping.put("烧录不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("烧录工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusBurn = updateDataVersions("5ec2995c", fieldMapping, 84946614, 84946544);
        for (ApsMesTotal apsMesTotal : plusBurn) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("主板程序烧录");
        }
        apsMesTotals.addAll(plusBurn);
    }

    public void PlusVersion() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次分板完成数", "burnInCompletionQuantity");
        fieldMapping.put("分板合格数", "burnQualifiedCount");
        fieldMapping.put("分板不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("分板治具编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusVersion = updateDataVersions("e48e6720", fieldMapping, 84946836, 84946835);
        for (ApsMesTotal apsMesTotal : plusVersion) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("主板分板");
        }
        apsMesTotals.addAll(plusVersion);
    }

    public void PlusInstallationBoard() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
        fieldMapping.put("包装终检合格数", "burnQualifiedCount");
        fieldMapping.put("包装终检不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusInstallationBoard = updateDataVersions("6f3d5384", fieldMapping, 84947068, 84947067);
        for (ApsMesTotal apsMesTotal : plusInstallationBoard) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("主板安装");
        }
        apsMesTotals.addAll(plusInstallationBoard);
    }

    public void PlusFixed() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次安装完成数", "burnInCompletionQuantity");
        fieldMapping.put("安装合格数", "burnQualifiedCount");
        fieldMapping.put("安装不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> fixed = updateDataVersions("40932f7f", fieldMapping, 84951220, 84951219);
        for (ApsMesTotal apsMesTotal : fixed) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("主板固定");
        }
        apsMesTotals.addAll(fixed);
    }

    public void PlusSnLabeling() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次粘贴完成数", "burnInCompletionQuantity");
        fieldMapping.put("粘贴合格数", "burnQualifiedCount");
        fieldMapping.put("粘贴不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusSnLabeling = updateDataVersions("6ce0b9f3", fieldMapping, 84947282, 84947281);
        for (ApsMesTotal apsMesTotal : plusSnLabeling) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("粘贴SN标签");
        }
        apsMesTotals.addAll(plusSnLabeling);
    }

    public void PlusUltrasonicWelding() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次焊接完成数", "burnInCompletionQuantity");
        fieldMapping.put("焊接合格数", "burnQualifiedCount");
        fieldMapping.put("焊接不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> ultrasonicWelding = updateDataVersions("d65fa0be", fieldMapping, 84947985, 84947984);
        for (ApsMesTotal apsMesTotal : ultrasonicWelding) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("超声波焊接");
        }
        apsMesTotals.addAll(ultrasonicWelding);
    }

    public void PlusShellWiring() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次生产完成数", "burnInCompletionQuantity");
        fieldMapping.put("生产合格数", "burnQualifiedCount");
        fieldMapping.put("生产不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> shellWiring = updateDataVersions("c2c89bcd", fieldMapping, 84951454, 84951453);
        for (ApsMesTotal apsMesTotal : shellWiring) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("后壳穿线");
        }
        apsMesTotals.addAll(shellWiring);
    }

    public void PlusShellGluing() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次生产完成数", "burnInCompletionQuantity");
        fieldMapping.put("生产合格数", "burnQualifiedCount");
        fieldMapping.put("生产不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> shellGluing = updateDataVersions("805f21e8", fieldMapping, 84948447, 84948446);
        for (ApsMesTotal apsMesTotal : shellGluing) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("后壳点胶");
        }
        apsMesTotals.addAll(shellGluing);
    }

    public void PlusTerminalConnection() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次生产完成数", "burnInCompletionQuantity");
        fieldMapping.put("生产合格数", "burnQualifiedCount");
        fieldMapping.put("生产不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码 ", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> terminalConnection = updateDataVersions("7a017004", fieldMapping, 84948216, 84948215);
        for (ApsMesTotal apsMesTotal : terminalConnection) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("连接端子");
        }
        apsMesTotals.addAll(terminalConnection);
    }


    public void PlusCalibration() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次校准测试完成数", "burnInCompletionQuantity");
        fieldMapping.put("校准合格数", "burnQualifiedCount");
        fieldMapping.put("校准不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("测试工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusCalibration = updateDataVersions("33152cc3", fieldMapping, 84947501, 84947500);
        for (ApsMesTotal apsMesTotal : plusCalibration) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("校准测试");
        }
        apsMesTotals.addAll(plusCalibration);
    }

    public void PlusSpotTesting() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次校准测试完成数", "burnInCompletionQuantity");
        fieldMapping.put("校准合格数", "burnQualifiedCount");
        fieldMapping.put("校准不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("测试工装编号", "burnFixtureNumber");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> spotTesting = updateDataVersions("2d3f8543", fieldMapping, 84949387, 84949386);
        for (ApsMesTotal apsMesTotal : spotTesting) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("定点测试");
        }
        apsMesTotals.addAll(spotTesting);
    }

    public void PlusPackagingTest() throws Exception {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("生产订单编号", "productionOrderNumber");
        fieldMapping.put("本次包装终检完成数", "burnInCompletionQuantity");
        fieldMapping.put("包装终检合格数", "burnQualifiedCount");
        fieldMapping.put("包装终检不合格数", "unBurnQualifiedCount");
        fieldMapping.put("物料编码", "materialCode");
        fieldMapping.put("物料名称", "materialName");
        fieldMapping.put("订单总数", "totalNumber");
        List<ApsMesTotal> plusPackagingTest = updateDataVersions("b3464dcd", fieldMapping, 84947740, 84947739);
        for (ApsMesTotal apsMesTotal : plusPackagingTest) {
            apsMesTotal.setWorkpiece("tfmini-plus");
            apsMesTotal.setProcess("包装");
        }
        apsMesTotals.addAll(plusPackagingTest);
    }

    //获取最后一步未完成的订单
    private List<ApsMesTotal> unFinish() throws Exception {
        apsMesTotals = new ArrayList<>();
        SPackagingTest();
        LunaPackagingTest();
        PlusPackagingTest();
        List<ApsMesTotal> unFinished = apsMesTotals.stream()
                .filter(apsMesTotal -> apsMesTotal.getBurnInCompletionQuantity() == null)
                .collect(Collectors.toList());
        return unFinished;

    }

    @Override
    public Boolean updateDataVersions() throws Exception {
        apsMesTotals = new ArrayList<>();
        maxVersionIncr = this.getMaxVersionIncr();
        List<ApsMesTotal> unFinished = unFinish();
        SBurn();
        SVersion();
        SSnLabeling();
        SCalibration();
        SFixed();
        SInstallationBoard();
        LunaInstallationBoard();
        LunaBurn();
        LunaVersion();
        LunaSnLabeling();
        LunaCalibration();
        LunaFixed();
        PlusFixed();
        PlusShellGluing();
        PlusUltrasonicWelding();
        PlusBurn();
        PlusCalibration();
        PlusInstallationBoard();
        PlusVersion();
        PlusSpotTesting();
        PlusSnLabeling();
        PlusTerminalConnection();
        PlusShellWiring();
        //mes中只保留未完成的订单
        apsMesTotals = apsMesTotals.stream()
                .filter(record -> unFinished.stream()
                        .anyMatch(unFinishedRecord ->
                                record.getProductionOrderNumber().equals(unFinishedRecord.getProductionOrderNumber())
                                        && record.getWorkpiece().equals(unFinishedRecord.getWorkpiece())
                        )
                )
                .collect(Collectors.toList());
        return saveBatch(apsMesTotals);
    }


}




