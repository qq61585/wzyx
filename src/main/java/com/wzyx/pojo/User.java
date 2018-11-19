package com.wzyx.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class User implements Serializable {
    private Integer userId;

    private String userName;

    private String password;

    private String photo;

    private Integer age;

    private Integer gender;

    private String phoneNumber;

    private String qqNumber;

    private String wechatNumber;

    private String email;

    private Integer totalPoints;

    private BigDecimal accountBalance;

    private Date createTime;

    private Date updateTime;

    private Integer role;

    private Integer status;

    public User(Integer userId, String userName, String password, String photo, Integer age, Integer gender, String phoneNumber, String qqNumber, String wechatNumber, String email, Integer totalPoints, BigDecimal accountBalance, Date createTime, Date updateTime, Integer role, Integer status) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.photo = photo;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.qqNumber = qqNumber;
        this.wechatNumber = wechatNumber;
        this.email = email;
        this.totalPoints = totalPoints;
        this.accountBalance = accountBalance;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.role = role;
        this.status = status;
    }

    public User() {
        super();
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
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo == null ? null : photo.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber == null ? null : qqNumber.trim();
    }

    public String getWechatNumber() {
        return wechatNumber;
    }

    public void setWechatNumber(String wechatNumber) {
        this.wechatNumber = wechatNumber == null ? null : wechatNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
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