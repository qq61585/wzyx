package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ISellerMaterialService {

    ServerResponse submitAuditMaterial(String phoneNumber, String password,
                                       String realName, String identifyNumber,
                                       String businessLicenseNumber,
                                       MultipartFile[] files, String path);

    ServerResponse getMaterialBySellerId(String sellerId);
}
