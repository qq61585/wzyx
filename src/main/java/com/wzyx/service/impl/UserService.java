package com.wzyx.service.impl;

import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.RedisExpireTime;
import com.wzyx.common.enumration.Role;
import com.wzyx.common.enumration.RoleStatus;
import com.wzyx.dao.UserMapper;
import com.wzyx.pojo.User;
import com.wzyx.service.IUserService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.MD5Utils;
import com.wzyx.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserService implements IUserService {


    @Autowired
    private UserMapper userMapper;

    /**
     * 完成用户登录，登录失败返回失败原因
     * 登陆成功，返回用户信息和authToken，并且将用户信息放到Redis数据库中保持登陆状态
     * @param phoneNumber
     * @param password
     * @return
     */
    @Override
    public ServerResponse login(String phoneNumber, String password) {
        User user = null;
        password = MD5Utils.MD5EncodeUtf8(password);
        user = userMapper.login(phoneNumber, password);
        if (user == null) {
            // 数据库中没有该用户, 进一步判断是手机号不存在还是密码错误
            user = userMapper.selectByPhoneNumber(phoneNumber);
            if (user != null) {
                //密码错误
                return ServerResponse.createByErrorMessage("password is wrong");
            }
            return ServerResponse.createByErrorMessage("phoneNumber doesn't exist");
        }
        //返回用户信息给前端前，置空密码, 生成随机的AUTH_TOKEN,将用户信息Json序列化，存入到Redis缓存中
        user.setPassword(null);
        String authToken = UUID.randomUUID().toString();
        String userStr = JsonUtil.obj2String(user);
        RedisPoolUtil.setEx(authToken, userStr, RedisExpireTime.USER_EXPIRE_TIME.getTime());
        return ServerResponse.createBySuccess(authToken, user);
    }

    /**
     * 完成用户注册，首先进行验证码的校验，然后是用户信息的添加
     * @param phoneNumber
     * @param password
     * @param role 需要注册的角色的类别，0-普通用户，1-商家，不同角色设置不同的信息
     * @param verification 验证码
     * @return
     */
    @Override
    public ServerResponse register(String phoneNumber, String password, int role, String verification) {
        //进行验证码的校验，从Redis数据库中取出数据
        String realCheckCode = RedisPoolUtil.get(phoneNumber);
        if (realCheckCode == null || !realCheckCode.equals(verification)) {
            //验证码不存在，或者已经失效
            return ServerResponse.createByErrorMessage("验证码已经失效，请重新获取");
        }

        User user = null;
        user = userMapper.selectByPhoneNumber(phoneNumber);
        if (user == null) {
//            当前手机号还没有注册,添加手机号、密码、角色、是否审核等信息，普通用户默认已经审核，商家默认待审核
            user = new User();
            password = MD5Utils.MD5EncodeUtf8(password);
            user.setPassword(password);
            user.setPhoneNumber(phoneNumber);
            if (role == Role.USER.getCode()) {
//                普通用户注册，默认是已经审核过的
                user.setRole(role);
                user.setStatus(RoleStatus.CHECKED.getCode());
            } else if (role == Role.SELLER.getCode()) {
//                商家注册,默认是未审核的
                user.setRole(role);
                user.setStatus(RoleStatus.UNCHECKED.getCode());
            }
//            向表中插入数据
            int count = userMapper.insert(user);
            if (count == 0) {
                //生效行数为0 表示插入失败
                return ServerResponse.createByErrorMessage("服务器发生异常，请稍后再试");
            }
            user.setPassword(null);
            return ServerResponse.createBySuccess("用户注册成功", user);
        }
//        当前手机号已经注册，提示用户直接登录
        return ServerResponse.createByErrorMessage("当前手机号已经注册，请直接登录");
    }


}

















