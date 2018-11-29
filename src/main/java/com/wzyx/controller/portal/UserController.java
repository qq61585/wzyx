package com.wzyx.controller.portal;


import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.RedisExpireTime;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.pojo.User;
import com.wzyx.service.IUserService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.MD5Utils;
import com.wzyx.util.RedisPoolUtil;
import com.wzyx.util.VerificationCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


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
    @RequestMapping(value = "sendVerificationCode")
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
    @RequestMapping(value = "register")
    @ResponseBody
    public ServerResponse register(String phoneNumber, String password, String verificationCode) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password) || StringUtils.isBlank(verificationCode)) {
//          提供的注册信息为空，或者提供的角色信息不是普通用户，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.register(phoneNumber, MD5Utils.MD5EncodeUtf8(password), Role.USER.getCode(), verificationCode);
    }

    /**
     * 用户登录
     * @param phoneNumber
     * @param password
     * @return
     */
    @RequestMapping(value = "login")
    @ResponseBody
    public ServerResponse login(String phoneNumber, String password) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password)) {
//            输入参数为空，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.login(phoneNumber, MD5Utils.MD5EncodeUtf8(password), Role.USER.getCode());
    }

    /**
     * 用户登出
     * @param authToken
     * @return
     */
    @RequestMapping(value = "logout")
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
     * 检查要注册的手机号是否已经注册
     * @param phoneNumber
     * @return
     */
    @RequestMapping(value = "check_valid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.checkValid(phoneNumber);
    }


    /**
     * 更新个人的信息， 需要进行登录验证。横向越权，防止修改别人的信息。
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information")
    @ResponseBody
    public ServerResponse updateInformation(User user, String authToken) {
//        首先进行权限验证，和是否登录的验证
        if (user == null || StringUtils.isBlank(authToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        String userString = RedisPoolUtil.get(authToken);
        if (userString == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
//      检查要修改的信息是否是当前用户的
        User currentUser = JsonUtil.str2Object(userString, User.class);
/*        if (!currentUser.getPhoneNumber().equals(user.getPhoneNumber())) {
            return ServerResponse.createByErrorMessage("要修改的信息非法");
        }*/
//        要修改的信息是当前用户的,设置角色，等不允许被修改的字段信息，防止恶意修改。
        user.setRole(Role.USER.getCode());
        user.setUserId(currentUser.getUserId());
        user.setStatus(currentUser.getStatus());
        user.setPhoneNumber(currentUser.getPhoneNumber());
        user.setAccountBalance(currentUser.getAccountBalance());
        return userService.updateInformation(user);
    }

    /**
     * 通过token来获取用户信息, 使用前检查用户是否登录
     * @param authToken
     * @return
     */
    @RequestMapping(value = "get_information", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getInformation(String authToken) {
        if (StringUtils.isBlank(authToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String userString = RedisPoolUtil.get(authToken);
        if (StringUtils.isBlank(userString)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
//        用户已经登录，返回个人信息
        User user = JsonUtil.str2Object(userString, User.class);
        return ServerResponse.createBySuccessData(user);
    }

    /**
     * 在用户知道原始密码的情况下更改密码
     * @param authToken  用户的token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    @RequestMapping(value = "reset_password", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String authToken, String oldPassword,
                                        String newPassword) {
        if (StringUtils.isBlank(authToken) || StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        String userString = RedisPoolUtil.get(authToken);
        if (StringUtils.isBlank(userString)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
//        用户已经登录，并且要修改的信息有效，不为空。调用业务层来修改密码
        User user = JsonUtil.str2Object(userString, User.class);
        return userService.resetPassword(user.getUserId(), MD5Utils.MD5EncodeUtf8(oldPassword), MD5Utils.MD5EncodeUtf8(newPassword));
    }

    /**
     * 用户忘记了自己的旧密码，通过手机验证码的方式来修改新的密码。 调用此接口前先调用发送验证码的接口
     * 方法先验证要修改的用户的手机号和 验证码是否匹配，匹配的进行密码修改
     * @param phoneNumber
     * @param newPassword
     * @param verificationCode
     * @return
     */
    @RequestMapping(value = "forget_reset_password", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetResetPassword(String phoneNumber, String newPassword, String verificationCode) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(newPassword) || StringUtils.isBlank(verificationCode)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
//        方法的参数有效（不为空）
        String redisVerificationCode = RedisPoolUtil.get(phoneNumber);
        if (!StringUtils.isBlank(redisVerificationCode) && redisVerificationCode.equals(verificationCode)) {
//            Redis数据库中有验证码的缓存，并且该手机号和缓存的验证码匹配,进行密码的修改
            return userService.forgetResetPassword(phoneNumber, MD5Utils.MD5EncodeUtf8(newPassword));

        }
//        验证码不匹配
        return ServerResponse.createByErrorMessage("验证码填写错误");
    }

    /**
     *  上传头像， 用户通过file或者url来上传头像，不能同时为空
     * @param request
     * @param file
     * @param authToken
     * @param url
     * @return
     */
    @RequestMapping(value = "update_photo", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updatePhoto(HttpServletRequest request,
                                      @RequestParam(value = "file") MultipartFile file,
                                      String authToken, String url) {
            if (StringUtils.isBlank(authToken) || file.isEmpty() && StringUtils.isBlank(url)) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            String userString = RedisPoolUtil.get(authToken);
            if (StringUtils.isBlank(userString)) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
            }
            User user = JsonUtil.str2Object(userString, User.class);
            if (!file.isEmpty()) {
                String path = request.getServletContext().getRealPath("img");
                return userService.updatePhoto(user.getUserId(), file, path);
            }
            return userService.updatePhotoByUrl(user.getUserId(), url);
    }




}












