package com.wzyx.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Product {
    private Integer pId;

    private String pName;

    private Integer pPrice;

    private String pContent;

    private Integer userId;

    private Integer pCapacity;

    private String pImage;

    private Double pLonggitude;

    private Double pLatitude;

    private String pLocation;

    private Date createTime;

    private Date updateTime;

    private Integer pState;

    private Integer pCate;

    private String pImagelist;
@JsonFormat(pattern = "yyyy-MM-dd")
    private Date pStarttime;
@JsonFormat(pattern = "yyyy-MM-dd")
    private Date pEndtime;

    private Integer pHasadded;

    public Product(Integer pId, String pName, Integer pPrice, String pContent, Integer userId, Integer pCapacity, String pImage, Double pLonggitude, Double pLatitude, String pLocation, Date createTime, Date updateTime, Integer pState, Integer pCate, String pImagelist, Date pStarttime, Date pEndtime, Integer pHasadded) {
        this.pId = pId;
        this.pName = pName;
        this.pPrice = pPrice;
        this.pContent = pContent;
        this.userId = userId;
        this.pCapacity = pCapacity;
        this.pImage = pImage;
        this.pLonggitude = pLonggitude;
        this.pLatitude = pLatitude;
        this.pLocation = pLocation;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.pState = pState;
        this.pCate = pCate;
        this.pImagelist = pImagelist;
        this.pStarttime = pStarttime;
        this.pEndtime = pEndtime;
        this.pHasadded = pHasadded;
    }

    public Product() {
        super();
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName == null ? null : pName.trim();
    }

    public Integer getpPrice() {
        return pPrice;
    }

    public void setpPrice(Integer pPrice) {
        this.pPrice = pPrice;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent == null ? null : pContent.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getpCapacity() {
        return pCapacity;
    }

    public void setpCapacity(Integer pCapacity) {
        this.pCapacity = pCapacity;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage == null ? null : pImage.trim();
    }

    public Double getpLonggitude() {
        return pLonggitude;
    }

    public void setpLonggitude(Double pLonggitude) {
        this.pLonggitude = pLonggitude;
    }

    public Double getpLatitude() {
        return pLatitude;
    }

    public void setpLatitude(Double pLatitude) {
        this.pLatitude = pLatitude;
    }

    public String getpLocation() {
        return pLocation;
    }

    public void setpLocation(String pLocation) {
        this.pLocation = pLocation == null ? null : pLocation.trim();
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

    public Integer getpState() {
        return pState;
    }

    public void setpState(Integer pState) {
        this.pState = pState;
    }

    public Integer getpCate() {
        return pCate;
    }

    public void setpCate(Integer pCate) {
        this.pCate = pCate;
    }

    public String getpImagelist() {
        return pImagelist;
    }

    public void setpImagelist(String pImagelist) {
        this.pImagelist = pImagelist == null ? null : pImagelist.trim();
    }

    public Date getpStarttime() {
        return pStarttime;
    }

    public void setpStarttime(Date pStarttime) {
        this.pStarttime = pStarttime;
    }

    public Date getpEndtime() {
        return pEndtime;
    }

    public void setpEndtime(Date pEndtime) {
        this.pEndtime = pEndtime;
    }

    public Integer getpHasadded() {
        return pHasadded;
    }

    public void setpHasadded(Integer pHasadded) {
        this.pHasadded = pHasadded;
    }
}