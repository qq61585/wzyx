package com.wzyx.controller.admin;

import com.github.pagehelper.StringUtil;
import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.pojo.User;
import com.wzyx.service.ISellerMaterialService;
import com.wzyx.service.IUserService;
import com.wzyx.util.MD5Utils;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 平台管理员的后端接口类，通过web前端来进行访问，单机架构,
 */
@Controller
@RequestMapping("/admin/user/")
public class AdminUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISellerMaterialService materialService;

    /** 管理员使用手机号+密码登录
     *
     * @param phoneNumber
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public ServerResponse login(String phoneNumber, String password, HttpSession session) {
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password)) {
//            输入参数为空，返回非法参数
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
//        要登录的用户的角色应该是管理员
        ServerResponse response = userService.login(phoneNumber, MD5Utils.MD5EncodeUtf8(password), Role.ADMIN.getCode());
//        把角色的信息存储到session中
        if (response.isSuccess()) {
            session.setAttribute(Role.ADMIN.getDesc(), response.getData());
            return ServerResponse.createBySuccess("登录成功", response.getData());
        }
        return response;
    }

    /**
     * 管理员登出
     * @param session
     * @return
     */
    @RequestMapping("logout")
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(Role.ADMIN.getDesc());
        return ServerResponse.createBySuccessMessage("登出成功");
    }


    /**
     *  查询用户信息的功能，查询指定状态、指定类别的用户的信息，例如未审核的商家用户，查询参数为可选的，可以都为空
     *  以列表的形式进行返回
     * @param userType 要查询的用户类型 0-普通用户 1-商家 2-管理员
     * @param userStatus 要查询的用户的状态 0-未审核 1-审核通过 2-审核未通过
     * @param pageNumber 要查询的页数
     * @param pageSize  每页的大小
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session,@RequestParam(value = "role", required = false) Integer userType,
                               @RequestParam(value = "status", required = false) Integer userStatus,
                               @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
//      管理员已经登录，下面执行查询业务
        return userService.getUserList(userType, userStatus, pageNumber, pageSize);
    }

    /**
     * 更改用户的状态,  首先判断管理员是否登录，确认登录之后执行更改状态操作，这里可以完成对用户状态的修改
     * @param session
     * @param userId
     * @param userStatus
     * @return
     */
    @RequestMapping(value = "update_user_status", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateUserStatus(HttpSession session, @RequestParam(value = "userId", required = true) Integer userId,
                                           @RequestParam(value = "status", required = true) Integer userStatus) {
        if (userId == null || userStatus == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        User user = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return userService.updateUserStatus(userId, userStatus);
    }

    /**
     * 管理员添加用户信息, 这里只是添加必须的信息，详细的信息通过用户更新个人信息的接口来实现。
     * @param phoneNumber
     * @param password
     * @param role    新增用户的角色， 默认为 0-普通用户
     * @param status    新增用户的状态 默认为 1-审核通过
     * @return
     */
    @RequestMapping(value = "add_user")
    @ResponseBody
    public ServerResponse addUser(HttpSession session, String phoneNumber, String password,
                                  @RequestParam(value = "role", defaultValue = "0") Integer role,
                                  @RequestParam(value = "status", defaultValue = "1") Integer status) {
        User user = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(password)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        User newUser = new User();
        newUser.setPhoneNumber(phoneNumber);
        newUser.setPassword(MD5Utils.MD5EncodeUtf8(password));
        newUser.setRole(role);
        newUser.setStatus(status);
        return userService.addUser(newUser);
    }

    /**
     *  管理员更新个人信息， 安全起见，管理员只能更新他自己的信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_user_info")
    @ResponseBody
    public ServerResponse updateUserInfo(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (user == null || currentUser.getUserId() != user.getUserId()) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.updateInformation(user);
    }


    @RequestMapping(value = "get_user_by_phoneNumber")
    @ResponseBody
    public ServerResponse getUserByPhoneNumber(HttpSession session, String phoneNumber) {
        User currentUser = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return userService.getUserByPhoneNumber(phoneNumber);
    }

    /**
     * 在已经登录的情况下，通过旧密码来修改密码
     * @param session
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "reset_password")
    @ResponseBody
    public ServerResponse resetPassword(HttpSession session, String oldPassword, String newPassword) {
        User currentUser = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.resetPassword(currentUser.getUserId(), MD5Utils.MD5EncodeUtf8(oldPassword), MD5Utils.MD5EncodeUtf8(newPassword));
    }

    @RequestMapping(value = "get_audit_material")
    @ResponseBody
    public ServerResponse getAuditMaterial(String sellerId, HttpSession session) {
        User currentUser = (User) session.getAttribute(Role.ADMIN.getDesc());
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (StringUtils.isBlank(sellerId)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return materialService.getMaterialBySellerId(sellerId);
    }

}

















