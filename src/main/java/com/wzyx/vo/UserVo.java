package com.wzyx.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 展示给管理员查看的用户视图对象，隐含一些用户隐私信息
 */
public class UserVo implements Serializable {
    private Integer userId;
    private String userName;
    private Integer gender;
    private String phoneNumber;
    private Integer totalPoints;
    private BigDecimal accountBalance;
    private Date createTime;
    private Date updateTime;
    private Integer role;
    private Integer status;

    public UserVo() {
    }

    public UserVo(Integer userId, String userName, Integer gender, String phoneNumber, Integer totalPoints, BigDecimal accountBalance, Date createTime, Date updateTime, Integer role, Integer status) {
        this.userId = userId;
        this.userName = userName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.totalPoints = totalPoints;
        this.accountBalance = accountBalance;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.role = role;
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
