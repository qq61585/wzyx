package com.wzyx.service.impl;


import com.google.common.collect.Lists;
import com.wzyx.common.ServerResponse;
import com.wzyx.service.IFileService;
import com.wzyx.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service(value = "iFileService")
public class FileService implements IFileService {

    private static Logger log = LoggerFactory.getLogger(FileService.class);

    @Override
    public String uploadFile(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffixName;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.setWritable(true);
            dir.mkdirs();
        }

        File targetFile = new File(path, newFileName);
        try {
            file.transferTo(targetFile);
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
        } catch (IOException e) {
            log.error("上传文件异常",e);
            return null;
        }

        targetFile.delete();
        return targetFile.getName();
    }
}
