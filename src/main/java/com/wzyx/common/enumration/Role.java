package com.wzyx.common.enumration;

/**
 * 用来区分不同类别用户的枚举类
 */
public enum Role {

    USER(0, "USER"),//普通用户
    SELLER(1, "SELLER"), //商家
    ADMIN(2, "ADMIN"); // 管理员

    private int code;
    private  String desc;

    Role(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
