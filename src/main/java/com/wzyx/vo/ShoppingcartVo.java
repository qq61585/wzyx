package com.wzyx.vo;

public class ShoppingcartVo {
    public ShoppingcartVo(){}
    public ShoppingcartVo(Integer pId,String pName, String user_name, Integer sNumbers, String pImage, double pPrice) {
        this.pID = pId;
        this.pName = pName;
        this.user_name = user_name;
        this.sNumbers = sNumbers;
        this.pImage = pImage;
        this.pPrice = pPrice;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Integer getsNumbers() {
        return sNumbers;
    }

    public void setsNumbers(Integer sNumbers) {
        this.sNumbers = sNumbers;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public double getpPrice() {
        return pPrice;
    }

    public void setpPrice(double pPrice) {
        this.pPrice = pPrice;
    }

    public Integer getpID() {
        return pID;
    }

    public void setpID(Integer pID) {
        this.pID = pID;
    }

    private Integer pID;

    private String pName;

    private String user_name;

    private Integer sNumbers;

    private String pImage;

    private double pPrice;


}
