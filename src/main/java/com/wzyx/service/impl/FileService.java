package com.wzyx.service.impl;


import com.google.common.collect.Lists;
import com.wzyx.service.IFileService;
import com.wzyx.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service(value = "iFileService")
public class FileService implements IFileService {

    private static Logger log = LoggerFactory.getLogger(FileService.class);

    /**
     *
     * @param file
     * @param path
     * @return
     */
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
        boolean success = false;
        try {
            file.transferTo(targetFile);
            success = FTPUtil.uploadFile(Lists.newArrayList(targetFile));
        } catch (Exception e) {
            log.error("上传文件异常",e);
            return null;
        }

        targetFile.delete();
        if (success) {
            return targetFile.getName();
        }
        return null;
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









