package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsFimRequest;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsFimRequestParam;
import com.benewake.system.entity.vo.ApsFimRequestVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.entity.ExcelFimRequest;
import com.benewake.system.excel.entity.ExcelFimRequestTemplate;
import com.benewake.system.excel.listener.FimRequestListener;
import com.benewake.system.excel.transfer.FimRequestVoTiExcelList;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsFimRequestMapper;
import com.benewake.system.service.ApsFimRequestService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.ResponseUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_fim_request】的数据库操作Service实现
 * @createDate 2023-12-01 09:34:02
 */
@Service
public class ApsFimRequestServiceImpl extends ServiceImpl<ApsFimRequestMapper, ApsFimRequest>
        implements ApsFimRequestService {

    @Autowired
    private ApsFimRequestMapper fimRequestMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FimRequestVoTiExcelList fimRequestVoTiExcelList;

    @Override
    public PageResultVo<ApsFimRequestVo> getFimRequestPage(Integer page, Integer size) {
        Page<ApsFimRequestVo> pageParam = new Page<>();
        pageParam.setSize(size);
        pageParam.setCurrent(page);
        Page<ApsFimRequestVo> fimRequestVoPage = fimRequestMapper.getFimRequestPage(pageParam);
        PageResultVo<ApsFimRequestVo> pageResultVo = buildPageListVo(fimRequestVoPage);
        return pageResultVo;
    }

    @Override
    public Boolean addOrUpdateFimRequest(ApsFimRequestParam fimRequestParam) {
        ApsFimRequest fimRequest = builFimRequestPo(fimRequestParam);
        if (fimRequestParam.getId() == null) {
            return save(fimRequest);
        } else {
            return updateById(fimRequest);
        }
    }

    @Override
    public void downloadFimRequest(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "fim需求");
            Page<ApsFimRequestVo> pageParam = new Page<>();
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                long size = count(null);
                pageParam.setSize(size);
                pageParam.setCurrent(1);
            } else {
                pageParam.setSize(downloadParam.getSize());
                pageParam.setCurrent(downloadParam.getPage());
            }
            Page<ApsFimRequestVo> fimRequestVoPage = fimRequestMapper.getFimRequestPage(pageParam);
            List<ApsFimRequestVo> records = fimRequestVoPage.getRecords();
            List<ExcelFimRequest> excelFimRequests = Collections.emptyList();
            if (CollectionUtils.isNotEmpty(records)) {
                excelFimRequests = fimRequestVoTiExcelList.convert(records);
            }
            EasyExcel.write(response.getOutputStream(), ExcelFimRequest.class).sheet("sheet1")
                    .doWrite(excelFimRequests);
        } catch (Exception e) {
            log.error("导出fim需求表失败" + e.getMessage());
            throw new BeneWakeException("导出fim需求表失败");
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelFimRequestTemplate.class, new FimRequestListener(this, type))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            log.error("导入fim需求表失败" + e.getMessage());
            if (e instanceof BeneWakeException) {
                throw new BeneWakeException(e.getMessage());
            }
            throw new BeneWakeException("导入fim需求表失败");
        }
        return true;
    }

    private ApsFimRequest builFimRequestPo(ApsFimRequestParam fimRequestParam) {
        if (fimRequestParam == null) {
            return null;
        }
        ApsFimRequest fimRequest = new ApsFimRequest();
        fimRequest.setId(fimRequestParam.getId());
        fimRequest.setFDocumentNumber(fimRequestParam.getDocumentNumber());
        fimRequest.setFCreator(fimRequestParam.getFCreator());
        fimRequest.setFMaterialCode(fimRequestParam.getMaterialCode());
        fimRequest.setFCustomerName(fimRequestParam.getCustomerName());
        fimRequest.setFSalesperson(fimRequestParam.getSalesperson());
        fimRequest.setFQuantity(fimRequestParam.getQuantity());
        fimRequest.setFExpectedDeliveryDate(fimRequestParam.getExpectedDeliveryDate());
        fimRequest.setFDocumentType(fimRequestParam.getDocumentType());
        return fimRequest;
    }

    private PageResultVo<ApsFimRequestVo> buildPageListVo(Page<ApsFimRequestVo> fimRequestVoPage) {
        PageResultVo<ApsFimRequestVo> listRestVo = new PageResultVo<>();
        listRestVo.setList(fimRequestVoPage.getRecords());
        listRestVo.setPage(Math.toIntExact(fimRequestVoPage.getCurrent()));
        listRestVo.setSize(Math.toIntExact(fimRequestVoPage.getSize()));
        listRestVo.setTotal(fimRequestVoPage.getTotal());
        listRestVo.setPages(fimRequestVoPage.getPages());
        return listRestVo;
    }
}




