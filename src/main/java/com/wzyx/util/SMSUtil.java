package com.wzyx.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用来发送短信的工具类
 */
public class SMSUtil {

    private static Logger log = LoggerFactory.getLogger(SMSUtil.class);

    //发送短信的客户端， 第一次使用时进行初始化，静态变量
    private static IAcsClient acsClient;

    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    // 配置个人阿里云的AK，保密信息
    private static String accessKeyId = PropertiesUtil.getProperty("dysms.accessKeyId");
    private static String accessKeySecret = PropertiesUtil.getProperty("dysms.accessKeySecret");

    //默认的超时时间，分为连接超时和读取超时
    private static String defaultConnectTimeout = PropertiesUtil.getProperty("dysms.defaultConnectTimeout");
    private static String defaultReadTimeout = PropertiesUtil.getProperty("dysms.defaultReadTimeout");

    //发送验证码短信的配置信息
    private static String signName = PropertiesUtil.getProperty("dysms.signName");
    private static String templateCode = PropertiesUtil.getProperty("dysms.templateCode");


//    初始化短信发送客户端的一些配置
    static {
        //可通过修改wzyx.properties来调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", defaultConnectTimeout);
        System.setProperty("sun.net.client.defaultReadTimeout", defaultReadTimeout);

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    try {
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        acsClient = new DefaultAcsClient(profile);
    } catch (ClientException e) {
//        初始化失败，打印错误日志
        log.error("SMS init Error", e);
    }

    }


    /**
     * 向指定手机号发送指定验证码
     * @param phoneNumber 手机号
     * @param verificationCode 验证码，由服务调用方提供
     * @return 返回发送短信的相应对象
     */
    public static SendSmsResponse sendSms(String phoneNumber, String verificationCode) {
        String templateParam = "{\"code\":\"" + verificationCode + "\"}";
        return sendSms(phoneNumber, signName, templateCode, templateParam);
    }

    /**
     * 执行发送短信的工具类，可以发送指定手机号，签名、模板代码的、模板变量的信息,私有化，被类里面其他更具体的方法调用，例如发送验证码的方法
     * @param phoneNumber 手机号
     * @param signName    短信签名，提前在阿里云申请
     * @param templateCode 模板代码，提前在阿里云申请
     * @param templateParam 模板变量，和具体的模板有关，用来代替模板里面的变量值
     * @return
     */
    private static  SendSmsResponse sendSms(String phoneNumber, String signName, String templateCode, String templateParam) {


        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号  例如 "18629501095"
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到  例如 "debug"
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到， 例如 "SMS_151178564"
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"您的验证码为${code}"时,此处的值用来填充${code}变量的值，示例"{\"code\":\"123456\"}"
        request.setTemplateParam(templateParam);

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者， 这里暂时设置为用户的手机号
        request.setOutId(phoneNumber);

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("send SMS fail", e);
        }

        return sendSmsResponse;
    }

}
