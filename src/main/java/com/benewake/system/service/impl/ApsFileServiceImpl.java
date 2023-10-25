package com.benewake.system.service.impl;

import com.benewake.system.service.ApsFileService;
import com.benewake.system.utils.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ApsFileServiceImpl implements ApsFileService {
    @Value("${myPython.startClass.integrityCheckerFile.fileDirectory}")
    private String FILE_DIRECTORY;

    @Override
    public ResponseEntity<Resource> ApsIntegrityCheckeFile() {
        String filename = "不完整数据统计.xlsx";
        File file = new File(FILE_DIRECTORY + filename);

        if (!file.exists()) {
            // 文件不存在，返回404
            return ResponseEntity.notFound().build();
        }

        try {
            // 获取当前日期并格式化为字符串
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String formattedDate = dateFormat.format(new Date());

            // 文件名格式：不完整数据统计__yyyyMMddHHmmss.xlsx
            String finalFileName = filename.substring(0, filename.lastIndexOf(".")) + "__" + formattedDate + ".xlsx";
            String encodedFileName = URLEncoder.encode(finalFileName, "UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(filename);
            headers.setContentType(mediaType);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理编码或文件读取异常
        }

        return ResponseEntity.notFound().build();
    }
}
