package com.wzyx.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import java.io.IOException;
public class TencentSMSUtil {

    // 短信应用SDK AppID
   private static final int appid = Integer.parseInt(PropertiesUtil.getProperty("tencentsms.appId")); // 1400开头
    // 短信应用SDK AppKey
   private static final String appkey = PropertiesUtil.getProperty("tencentsms.appKey");
    // 需要发送短信的手机号码
    // 短信模板ID，需要在短信应用中申请
   private static final int templateId = Integer.parseInt(PropertiesUtil.getProperty("tencentsms.templateId")); // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
     //templateId7839对应的内容是"您的验证码是: {1}"
   //   名
   private static final String smsSign = PropertiesUtil.getProperty("tencentsms.smsSign"); // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台申请。

    private static final String expireTime = PropertiesUtil.getProperty("tencentsms.expireTime");

    private  static final SmsSingleSender ssender = new SmsSingleSender(appid, appkey);

    public static void sendSMS(String phoneNumber, String verificationCode) {
        try {
            String[] params = {verificationCode, expireTime};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            String[] phoneNumbers = new String[] {phoneNumber};

            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }


}
