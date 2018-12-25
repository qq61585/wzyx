package com.wzyx.pojo;

import java.util.Date;

public class Shoppingcart {
    private Integer sId;

    private Integer pId;

    private Integer userId;

    private Integer sNumber;

    private Date createTime;

    private Date updateTime;

    public Shoppingcart(Integer sId, Integer pId, Integer userId, Integer sNumber, Date createTime, Date updateTime) {
        this.sId = sId;
        this.pId = pId;
        this.userId = userId;
        this.sNumber = sNumber;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Shoppingcart() {
        super();
    }

    public Integer getsId() {
        return sId;
    }

    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getsNumber() {
        return sNumber;
    }

    public void setsNumber(Integer sNumber) {
        this.sNumber = sNumber;
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