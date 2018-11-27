package com.wzyx.controller.backend;

import com.sun.prism.shader.FillEllipse_ImagePattern_AlphaTest_Loader;
import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.pojo.User;
import com.wzyx.service.ISellerMaterialService;
import com.wzyx.service.IUserService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.MD5Utils;
import com.wzyx.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 商家的用户端接口
 */
@Controller
@RequestMapping("/mobile/seller/user")
public class SellerUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISellerMaterialService materialService;

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
        return userService.login(phoneNumber, MD5Utils.MD5EncodeUtf8(password), Role.SELLER.getCode());
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
        return userService.register(phoneNumber, MD5Utils.MD5EncodeUtf8(password), Role.SELLER.getCode(), verificationCode);
    }

    /**
     * 更新商家的个人的信息， 需要进行登录验证。横向越权，防止修改别人的信息。
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
        user.setRole(Role.SELLER.getCode());
        user.setUserId(currentUser.getUserId());
        user.setStatus(currentUser.getStatus());
        user.setPhoneNumber(currentUser.getPhoneNumber());
        user.setAccountBalance(currentUser.getAccountBalance());
        return userService.updateInformation(user);
    }


    /**
     * 商家提交审核材料
     * @param phoneNumber  商家的手机号
     * @param password      密码
     * @param realName      真实姓名
     * @param identifyNumber 身份证号
     * @param businessLicenseNumber 营业执照号码
     * @param files   文件数组，依次为身份证正面照、身份证反面照、营业执照照片
     * @return
     */
    @RequestMapping(value = "submit_audit_material")
    @ResponseBody
    public ServerResponse submitAuditMaterial(String phoneNumber, String password, String realName,
                                              String identifyNumber, String businessLicenseNumber,
                                              @RequestParam(value = "files") MultipartFile[] files, HttpServletRequest request) {
        String path = request.getServletContext().getRealPath("img");
        return materialService.submitAuditMaterial(phoneNumber, MD5Utils.MD5EncodeUtf8(password), realName,identifyNumber,
                businessLicenseNumber, files, path);
    }

    /**
     * 用与测试返回图片的类
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "test")
    public void getPhoto(HttpServletResponse response) throws IOException {
        String filePath = "/Users/fengjl/Desktop/1.png";
        FileInputStream fis = new FileInputStream(new File(filePath));
        int length = fis.available();
        byte[] buffer = new byte[length];
        fis.read(buffer, 0, length);
        fis.close();

        OutputStream out =  response.getOutputStream();

        response.setContentType("image/png");
        out.write(buffer);
        out.close();
    }



}






















