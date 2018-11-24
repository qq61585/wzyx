package com.wzyx.common.enumration;

/**
 * 用来存放FTP服务器存放路径的枚举类
 */
public enum FTPPath {
    USER("img", "存放用户图像的文件");


    private String path;
    private String desc;

    FTPPath(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }
}
