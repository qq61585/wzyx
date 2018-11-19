package com.wzyx.controller.portal;


import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.RedisExpireTime;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.service.IUserService;
import com.wzyx.util.RedisPoolUtil;
import com.wzyx.util.VerificationCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/mobile/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 向指定手机号发送验证码
     * @param phoneNumber
     * @return
     */
    @RequestMapping(value = "sendVerificationCode", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse sendVerificationCode(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //使用验证码工具类生成并发送随机验证码， 将随机的验证码缓存到Redis数据库中
        String verificationCode = VerificationCodeUtil.sendVerificationCode(phoneNumber);
        RedisPoolUtil.setEx(phoneNumber, verificationCode, RedisExpireTime.VERIFICATION_CODE_EXPIRE_TIME.getTime());
        return ServerResponse.createBySuccessMessage("验证码发送成功");
    }

    /**
     * 用户注册，接受用户信息，以及验证码，对注册信息，进行非空验证后，由service层注册的业务逻辑
     * @param phoneNumber 手机号
     * @param password  密码
     * @param verificationCode  验证码
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(String phoneNumber, String password, String verificationCode) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password) || StringUtils.isBlank(verificationCode)) {
//          提供的注册信息为空，或者提供的角色信息不是普通用户，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.register(phoneNumber, password, Role.USER.getCode(), verificationCode);
    }

    /**
     * 用户登录
     * @param phoneNumber
     * @param password
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String phoneNumber, String password) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password)) {
//            输入参数为空，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.login(phoneNumber, password);
    }

    /**
     * 用户登出
     * @param authToken
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(String authToken) {
        if (StringUtils.isBlank(authToken)) {
//           无效的authToken，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
//        从Redis数据库中删除 authToken，消除登录状态
        RedisPoolUtil.del(authToken);
        return ServerResponse.createBySuccessMessage("logout success");
    }




}












