package com.wzyx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP工具类，用于向FTP服务器上传文件，主要是图片
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIP = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");


    public static boolean uploadFile(List<File> fileList) throws IOException {
        logger.info("开始上传文件件");
        FTPUtil ftpUtil = new FTPUtil(ftpIP, ftpUser, 21, ftpPassword);
        boolean success = ftpUtil.uploadFile("img", fileList);
        logger.info("结束上传文件");
        return success;
    }


    private  boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean success = true;
        FileInputStream fis = null;
        if (connectServer(this.ip, this.port, this.user, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }

        return success;
    }




    private String ip;
    private String user;
    private int port;
    private String password;

    private FTPClient ftpClient;

    public boolean connectServer(String ip, int port, String username, String password) {
        ftpClient = new FTPClient();
        boolean success = false;
        try {
            ftpClient.connect(ip, port);
            success = ftpClient.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }



    public FTPUtil(String ip, String user, int port, String password) {
        this.ip = ip;
        this.user = user;
        this.port = port;
        this.password = password;
    }


    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        FTPUtil.logger = logger;
    }

    public static String getFtpIP() {
        return ftpIP;
    }

    public static void setFtpIP(String ftpIP) {
        FTPUtil.ftpIP = ftpIP;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPassword() {
        return ftpPassword;
    }

    public static void setFtpPassword(String ftpPassword) {
        FTPUtil.ftpPassword = ftpPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
