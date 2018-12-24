package com.wzyx.vo;

import java.util.Date;

public class OrderVo {
    public Integer getO_id() {
        return o_id;
    }

    public void setO_id(Integer o_id) {
        this.o_id = o_id;
    }

    private Integer o_id;
private String user_name; //客户名字
private String phone_number; //客户手机号码
private String shop_name; //商家名字
private String product_name;//商品名字
private double price; //单价
private double totalprice;//总价
private String image;//商品图片
private Integer cate;//商品分类

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCate() {
        return cate;
    }

    public void setCate(Integer cate) {
        this.cate = cate;
    }

    public Integer getS_number() {
        return s_number;
    }

    public void setS_number(Integer s_number) {
        this.s_number = s_number;
    }

    private Integer s_number;//商品参加人数
    public OrderVo() {
    }

    public OrderVo(Integer o_id,String user_name,String phone_number,String shop_name, String product_name,double price,String image,Integer cate,Integer s_number) {
        this.o_id = o_id;
        this.user_name = user_name;
        this.phone_number = phone_number;
        this.shop_name = shop_name;
        this.product_name = product_name;
        this.price = price;
        this.image = image;
        this.cate = cate;
        this.s_number = s_number;
    }

}

