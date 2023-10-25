package com.benewake.system.utils;

import org.springframework.http.MediaType;

public class MediaTypeUtils {

    public static MediaType getMediaTypeForFileName(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "xlsx":
                return MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            // 添加更多文件类型和对应的MediaType
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
