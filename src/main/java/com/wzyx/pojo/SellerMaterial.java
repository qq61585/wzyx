package com.wzyx.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 映射商家审核材料的pojo类
 */
public class SellerMaterial implements Serializable {
    private Integer sellerMaterialId;

    private Integer adminId;

    private Integer sellerId;

    private Integer phoneNumber;

    private String realName;

    private String identityNumber;

    private String identityCardFrontPhoto;

    private String identityCardBackPhoto;

    private String businessLicenseNumber;

    private String businessLicensePhoto;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public SellerMaterial(Integer sellerMaterialId, Integer adminId, Integer sellerId, Integer phoneNumber, String realName, String identityNumber, String identityCardFrontPhoto, String identityCardBackPhoto, String businessLicenseNumber, String businessLicensePhoto, Integer status, Date createTime, Date updateTime) {
        this.sellerMaterialId = sellerMaterialId;
        this.adminId = adminId;
        this.sellerId = sellerId;
        this.phoneNumber = phoneNumber;
        this.realName = realName;
        this.identityNumber = identityNumber;
        this.identityCardFrontPhoto = identityCardFrontPhoto;
        this.identityCardBackPhoto = identityCardBackPhoto;
        this.businessLicenseNumber = businessLicenseNumber;
        this.businessLicensePhoto = businessLicensePhoto;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public SellerMaterial() {
        super();
    }

    public Integer getSellerMaterialId() {
        return sellerMaterialId;
    }

    public void setSellerMaterialId(Integer sellerMaterialId) {
        this.sellerMaterialId = sellerMaterialId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber == null ? null : identityNumber.trim();
    }

    public String getIdentityCardFrontPhoto() {
        return identityCardFrontPhoto;
    }

    public void setIdentityCardFrontPhoto(String identityCardFrontPhoto) {
        this.identityCardFrontPhoto = identityCardFrontPhoto == null ? null : identityCardFrontPhoto.trim();
    }

    public String getIdentityCardBackPhoto() {
        return identityCardBackPhoto;
    }

    public void setIdentityCardBackPhoto(String identityCardBackPhoto) {
        this.identityCardBackPhoto = identityCardBackPhoto == null ? null : identityCardBackPhoto.trim();
    }

    public String getBusinessLicenseNumber() {
        return businessLicenseNumber;
    }

    public void setBusinessLicenseNumber(String businessLicenseNumber) {
        this.businessLicenseNumber = businessLicenseNumber == null ? null : businessLicenseNumber.trim();
    }

    public String getBusinessLicensePhoto() {
        return businessLicensePhoto;
    }

    public void setBusinessLicensePhoto(String businessLicensePhoto) {
        this.businessLicensePhoto = businessLicensePhoto == null ? null : businessLicensePhoto.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}