package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.mapper.ApsDailyDataUploadMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
* @author ASUS
* @description 针对表【aps_daily_data_upload】的数据库操作Service实现
* @createDate 2023-11-10 14:52:13
*/
@Service
public class ApsDailyDataUploadServiceImpl extends ServiceImpl<ApsDailyDataUploadMapper, ApsDailyDataUpload>
    implements ApsDailyDataUploadService{

    @Override
    public Boolean saveDataByExcel(MultipartFile file) {
//        file
        return null;
    }
}




