package com.wzyx.vo;

import com.wzyx.pojo.Product;

import java.util.Date;
import java.util.List;

public class ProductVo {
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

    private List<String> pImagelist;

    public String getpStarttime() {
        return pStarttime;
    }

    public void setpStarttime(String pStarttime) {
        this.pStarttime = pStarttime;
    }

    public String getpEndtime() {
        return pEndtime;
    }

    public void setpEndtime(String pEndtime) {
        this.pEndtime = pEndtime;
    }

    public Integer getpHasadded() {
        return pHasadded;
    }

    public void setpHasadded(Integer pHasadded) {
        this.pHasadded = pHasadded;
    }

    private String pStarttime;

    private String pEndtime;

    private Integer pHasadded;

    public ProductVo(Product p) {
        this.pId = p.getpId();
        this.pName = p.getpName();
        this.pPrice = p.getpPrice();
        this.userId = p.getUserId();
        this.pCapacity = p.getpCapacity();
        this.pImage = p.getpImage();
        this.pLonggitude = p.getpLonggitude();
        this.pLatitude = p.getpLatitude();
        this.pLocation = p.getpLocation();
        this.createTime = p.getCreateTime();
        this.updateTime = p.getUpdateTime();
        this.pState = p.getpState();
        this.pCate = p.getpCate();
        this.pContent = p.getpContent();
        this.pHasadded = p.getpHasadded();
    }

    public ProductVo() {
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

    public List<String> getpImagelist() {
        return pImagelist;
    }

    public void setpImagelist(List<String> pImagelist) {
        this.pImagelist = pImagelist == null ? null : pImagelist;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent == null ? null : pContent.trim();
    }
}