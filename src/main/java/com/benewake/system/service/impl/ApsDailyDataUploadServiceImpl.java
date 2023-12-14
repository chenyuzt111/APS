package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.entity.dto.ApsDailyDataUploadDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsDailyDataUploadParam;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.entity.ExcelDailyDataUploadTemplate;
import com.benewake.system.excel.listener.ExcelDailyDataUploadListener;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsDailyDataUploadMapper;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_daily_data_upload】的数据库操作Service实现
 * @createDate 2023-11-10 14:52:13
 */
@Slf4j
@Service
public class ApsDailyDataUploadServiceImpl extends ServiceImpl<ApsDailyDataUploadMapper, ApsDailyDataUpload>
        implements ApsDailyDataUploadService {

    @Autowired
    private ApsDailyDataUploadMapper dailyDataUploadMapper;

    @Autowired
    private ApsProcessNamePoolService processNamePoolService;



    @Override
    public PageResultVo<ApsDailyDataUploadDto> getDailyDataListPage(Integer page, Integer size) {
        Page<ApsDailyDataUploadDto> uploadPage = new Page<>();
        uploadPage.setCurrent(page);
        uploadPage.setSize(size);
        Page<ApsDailyDataUploadDto> dailyDataUploadPage = dailyDataUploadMapper.selectPageList(uploadPage);
        return getUploadPageListRestVo(dailyDataUploadPage);
    }

    @Override
    public void downloadDailyData(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "日别数据导出");
            List<ApsDailyDataUploadDto> dataListPageList = null;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Long count = getBaseMapper().selectCount(null);
                PageResultVo<ApsDailyDataUploadDto> dailyDataListPage = getDailyDataListPage(1, Math.toIntExact(count));
                dataListPageList = dailyDataListPage.getList();
            } else {
                PageResultVo<ApsDailyDataUploadDto> dailyDataListPage = getDailyDataListPage(downloadParam.getPage(), downloadParam.getSize());
                dataListPageList = dailyDataListPage.getList();
            }
            EasyExcel.write(response.getOutputStream(), ApsDailyDataUploadDto.class).sheet("sheet1")
                    .doWrite(dataListPageList);
        } catch (Exception e) {
            log.error("导出日别数据失败---" + LocalDateTime.now() + e.getMessage());
            throw new BeneWakeException("导出日别数据失败");
        }
    }

    @Override
    public Boolean importloadDailyData(Integer type, MultipartFile file) {
        try {
//            EasyExcel.read(file.getInputStream(),
//                            new ExcelDailyDataUploadListener(this ,type ,processNamePoolService))
//                    .sheet(0).headRowNumber(1).doRead();
            ExcelReader excelReader = EasyExcel.read(file.getInputStream(), ExcelDailyDataUploadTemplate.class,
                    new ExcelDailyDataUploadListener(this, type, processNamePoolService)).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BeneWakeException) {
                log.warn("导入日别数据失败" + LocalDateTime.now() + e.getMessage());
                throw new BeneWakeException(e.getMessage());
            }
            log.error("导入日别数据失败" + LocalDateTime.now() + e.getMessage());
            throw new BeneWakeException("导入日别数据失败未知异常");
        }
    }

    @Override
    public Boolean addOrUpdateDailyData(ApsDailyDataUploadParam dailyDataUploadParam) {
        ApsDailyDataUpload apsDailyDataUpload = dailDataParamToPo(dailyDataUploadParam);
        boolean res;
        if (dailyDataUploadParam.getId() == null) {
            res = save(apsDailyDataUpload);
        } else {
            res = updateById(apsDailyDataUpload);
        }
        return res;
    }

    @Override
    public Boolean removeByIdList(List<Integer> ids) {
        removeBatchByIds(ids);

        return true;
    }

    private ApsDailyDataUpload dailDataParamToPo(ApsDailyDataUploadParam dailyDataUploadParam) {
        ApsDailyDataUpload apsDailyDataUpload = new ApsDailyDataUpload();
        apsDailyDataUpload.setId(dailyDataUploadParam.getId());
        apsDailyDataUpload.setFOrderNumber(dailyDataUploadParam.getOrderNumber());
        apsDailyDataUpload.setFMaterialCode(dailyDataUploadParam.getMaterialCode());
        apsDailyDataUpload.setFProcessId(dailyDataUploadParam.getProcessId());
        apsDailyDataUpload.setFTotalQuantity(dailyDataUploadParam.getTotalQuantity());
        apsDailyDataUpload.setFCompletedQuantity(dailyDataUploadParam.getCompletedQuantity());
        apsDailyDataUpload.setFCapacityPsPuPp(dailyDataUploadParam.getCapacityPsPuPp());
        apsDailyDataUpload.setFRemainingQuantity(dailyDataUploadParam.getRemainingQuantity());
        apsDailyDataUpload.setFRemainingCapacity(dailyDataUploadParam.getRemainingCapacity());
        return apsDailyDataUpload;
    }

    private PageResultVo<ApsDailyDataUploadDto> getUploadPageListRestVo(Page<ApsDailyDataUploadDto> dailyDataUploadPage) {
        PageResultVo<ApsDailyDataUploadDto> pageResultVo = new PageResultVo<>();
        pageResultVo.setList(dailyDataUploadPage.getRecords());
        pageResultVo.setSize(Math.toIntExact(dailyDataUploadPage.getSize()));
        pageResultVo.setPages(dailyDataUploadPage.getPages());
        pageResultVo.setTotal(dailyDataUploadPage.getTotal());
        pageResultVo.setPage(Math.toIntExact(dailyDataUploadPage.getCurrent()));
        return pageResultVo;
    }


    @Override
    public void InsertDataIntoApsFimRequest(int a) {
        dailyDataUploadMapper.callInsertDataIntoApsFimRequest(a);
    }
}




