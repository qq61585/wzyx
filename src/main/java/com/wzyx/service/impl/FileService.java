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

    /**
     * 上传多个文件，并且以string[]的格式返回他们的文件名
     * @param files
     * @param path
     * @return
     */
    @Override
    public String[] uploadFiles(MultipartFile[] files, String path) {

        String[] fileNames = new String[files.length];
        int i = 0;
        for (MultipartFile file : files) {
            fileNames[i++] = uploadFile(file, path);
        }
        return fileNames;
    }
}









