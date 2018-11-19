package com.wzyx.util;

import java.security.MessageDigest;

/**
 * 对移动端传递过来的密码进行加密， 使用MD5的方式来进行加密
 */
public class MD5Utils {
//  盐值，防止密码被破解
    private static  String salt = PropertiesUtil.getProperty("md5.salt", "fsfsafasfasç31312");


    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写MD5,这里指定了加密所用的字符集名称
     *
     * @param origin
     * @param charsetname
     * @return
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString.toUpperCase();
    }

    /**
     * 对原始密码加上盐值，然后以 utf-8的字符集进行加密，返回加密后的字符串
     * @param origin    原始密码
     * @return      加密后的字符串
     */
    public static String MD5EncodeUtf8(String origin) {
        origin = origin + salt;
        return MD5Encode(origin, "utf-8");
    }


    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static void main(String[] args) {
        System.out.println(MD5EncodeUtf8("123456"));
    }

}
