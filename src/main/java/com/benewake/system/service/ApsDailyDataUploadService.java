package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.entity.dto.ApsDailyDataUploadDto;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_daily_data_upload】的数据库操作Service
 * @createDate 2023-11-10 14:52:13
 */
public interface ApsDailyDataUploadService extends IService<ApsDailyDataUpload> {


    Boolean importloadDailyData(Integer type, MultipartFile file);

    Boolean addOrUpdateDailyData(ApsDailyDataUploadParam dailyDataUploadParam);

    Boolean removeByIdList(List<Integer> ids);

    void InsertDataIntoApsFimRequest(int a);

    PageResultVo<ApsDailyDataUploadDto> getDailyDataListPage(Integer page, Integer size);

    void downloadDailyData(HttpServletResponse response, DownloadViewParams downloadParam);

    ResultColPageVo<ApsDailyDataUploadDto> getDailyDataFilter(Integer page, Integer size, QueryViewParams queryViewParams);

    List<Object> searchLike(SearchLikeParam searchLikeParam);
}
