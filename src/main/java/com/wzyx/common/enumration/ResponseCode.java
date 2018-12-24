package com.wzyx.common.enumration;

/**
 * 代表响应状态的枚举类
 */
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(2,"当前用户未登录，请登录后再进行操作"),
    ILLEGAL_ARGUMENT(3,"输入参数非法"),
    ILLEGAL_LIMIT(4,"用户权限不够");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code =code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}