package com.wzyx.pojo;

import java.util.Date;

public class Order {
    private Integer oId;

    private Integer userId;

    private Integer pId;

    private Integer oState;

    private Date oPaytime;

    private Date createTime;

    private Date updateTime;

    private Integer oPayway;

    public Order(Integer oId, Integer userId, Integer pId, Integer oState, Date oPaytime, Date createTime, Date updateTime, Integer oPayway) {
        this.oId = oId;
        this.userId = userId;
        this.pId = pId;
        this.oState = oState;
        this.oPaytime = oPaytime;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.oPayway = oPayway;
    }

    public Order() {
        super();
    }

    public Integer getoId() {
        return oId;
    }

    public void setoId(Integer oId) {
        this.oId = oId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Integer getoState() {
        return oState;
    }

    public void setoState(Integer oState) {
        this.oState = oState;
    }

    public Date getoPaytime() {
        return oPaytime;
    }

    public void setoPaytime(Date oPaytime) {
        this.oPaytime = oPaytime;
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

    public Integer getoPayway() {
        return oPayway;
    }

    public void setoPayway(Integer oPayway) {
        this.oPayway = oPayway;
    }
}