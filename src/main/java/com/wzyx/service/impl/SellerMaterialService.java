package com.wzyx.service.impl;

import com.google.common.collect.Lists;
import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.Role;
import com.wzyx.dao.SellerMaterialMapper;
import com.wzyx.dao.UserMapper;
import com.wzyx.pojo.SellerMaterial;
import com.wzyx.pojo.User;
import com.wzyx.service.IFileService;
import com.wzyx.service.ISellerMaterialService;
import com.wzyx.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service(value = "iSellerMaterialService")
public class SellerMaterialService implements ISellerMaterialService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IFileService fileService;

    @Autowired
    private SellerMaterialMapper materialMapper;


    /**
     * 商家提交审核材料， 首先查询要提交的商家是否已经注册，已经注册过的才能提交审核信息
     * 如果已经审核过了，就将图片文件上传到FTP服务器上面，将对应的url存储到表中
     *
     * @param phoneNumber 手机号
     * @param password      密码
     * @param realName      真实姓名
     * @param identifyNumber    身份证号码
     * @param businessLicenseNumber 营业执照号码
     * @param files         图片文件数组，依次为身份证正面照，身份证反面照，营业执照照片
     * @return
     */
    @Override
    public ServerResponse submitAuditMaterial(String phoneNumber, String password,
                                              String realName, String identifyNumber,
                                              String businessLicenseNumber,
                                              MultipartFile[] files, String path) {
//todo 传文件
        User user = userMapper.login(phoneNumber, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("手机号和密码不匹配");
        }
        if (user.getRole() != Role.SELLER.getCode()) {
            return ServerResponse.createByErrorMessage("请登录商家账号");
        }
//        上传图片
        String[] fileNames = fileService.uploadFiles(files, path);
//        上传成功，将图片的连接存储到数据库中
        SellerMaterial sellerMaterial = new SellerMaterial();
        sellerMaterial.setSellerId(user.getUserId());
        sellerMaterial.setPhoneNumber(phoneNumber);
        sellerMaterial.setRealName(realName);
        sellerMaterial.setIdentityNumber(identifyNumber);
        sellerMaterial.setBusinessLicenseNumber(businessLicenseNumber);
        sellerMaterial.setIdentityCardFrontPhoto(PropertiesUtil.getProperty("ftp.server.ftp.prefix") + File.separator + fileNames[0]);
        sellerMaterial.setIdentityCardBackPhoto(PropertiesUtil.getProperty("ftp.server.ftp.prefix") + File.separator + fileNames[1]);
        sellerMaterial.setBusinessLicensePhoto(PropertiesUtil.getProperty("ftp.server.ftp.prefix") + File.separator + fileNames[2]);
        int count = materialMapper.insertSelective(sellerMaterial);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("上传材料失败");
        }
        return ServerResponse.createBySuccessMessage("上传材料成功");
    }

    /**
     * 通过sellerId来获取指定商家的材料（包括审核过的和和待审核的）
     * @param sellerId
     * @return
     */
    @Override
    public ServerResponse getMaterialBySellerId(String sellerId) {

        List<SellerMaterial> materials;
        materials = materialMapper.selectBySellerId(sellerId);
        return ServerResponse.createBySuccessData(materials);
    }
}



















