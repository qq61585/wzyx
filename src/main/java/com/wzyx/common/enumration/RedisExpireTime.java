package com.wzyx.common.enumration;

/**
 * Redis数据库中各类信息失效时间的枚举类，
 */
public enum RedisExpireTime {

    USER_EXPIRE_TIME(60 * 60 * 24 * 7, "移动端的用户登录失效时间，7天"),
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
