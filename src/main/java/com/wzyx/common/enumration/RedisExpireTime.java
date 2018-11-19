package com.wzyx.common.enumration;

/**
 * Redis数据库中各类信息失效时间的枚举类，
 */
public enum RedisExpireTime {

    USER_EXPIRE_TIME(60 * 30, "用户登录失效时间，30分钟"),
    VERIFICATION_CODE_EXPIRE_TIME(60 * 10, "验证码失效时间，10分钟");

    private int time;
    private String desc;

    RedisExpireTime(int time, String desc) {
        this.time = time;
        this.desc = desc;
    }

    public int getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }
}
