package com.wzyx.controller.backend;

import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.service.IUserService;
import com.wzyx.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商家的用户端接口
 */
@Controller
@RequestMapping("/mobile/seller/user")
public class SellerUserController {

    @Autowired
    private IUserService userService;

    /**
     * 商家的登录
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
        return userService.login(phoneNumber, password, Role.SELLER.getCode());
    }

    /**
     * 商家的登出
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


    /**
     * 商家注册，接受商家手机信息，以及验证码，对注册信息，进行非空验证后，由service层注册的业务逻辑
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






}






















