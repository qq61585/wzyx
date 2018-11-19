package com.wzyx.common.enumration;

/**
 * 用来区分用户状态的枚举类
 */
public enum RoleStatus {

    UNCHECKED(0, "账号未审核"),
    CHECKED(1, "账号已审核"),
    CHECKED_AND_FAILED(2, "账号已审核，但未通过");

    private int code;
    private String desc;

    RoleStatus(int code, String desc) {
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
