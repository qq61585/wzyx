package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface IFileService {


    String uploadFile(MultipartFile file, String path);

    String[] uploadFiles(MultipartFile[] file, String path);

}
