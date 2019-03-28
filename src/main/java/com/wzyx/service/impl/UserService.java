package com.wzyx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.RedisExpireTime;
import com.wzyx.common.enumration.Role;
import com.wzyx.common.enumration.RoleStatus;
import com.wzyx.dao.SellerMaterialMapper;
import com.wzyx.dao.UserMapper;
import com.wzyx.pojo.SellerMaterial;
import com.wzyx.pojo.User;
import com.wzyx.service.IFileService;
import com.wzyx.service.IUserService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.PropertiesUtil;
import com.wzyx.util.RedisPoolUtil;
import com.wzyx.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service("iUserService")
public class UserService implements IUserService {


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private IFileService fileService;

    /**
     * 完成用户登录，登录失败返回失败原因
     * 登陆成功，返回用户信息和authToken，并且将用户信息放到Redis数据库中保持登陆状态
     * @param phoneNumber 登录账号的手机号
     * @param password    登录账号的密码
     * @param role        要登录的账号的角色
     * @return
     */
    @Override
    public ServerResponse login(String phoneNumber, String password, Integer role) {
        User user = null;
        user = userMapper.login(phoneNumber, password);
        if (user == null) {
            // 数据库中没有该用户, 进一步判断是手机号不存在还是密码错误
            user = userMapper.selectByPhoneNumber(phoneNumber);
            if (user != null) {
                //密码错误
                return ServerResponse.createByErrorMessage("密码错误");
            }
            return ServerResponse.createByErrorMessage("手机号不存在");
        }
//      验证要登录的用户的角色是否合法，该用户的角色由Controller层决定
        if (role != user.getRole()) {
            return ServerResponse.createByErrorMessage("要登录的用户没有权限");
        }
        if (user.getStatus() != RoleStatus.CHECKED.getCode()) {
            return ServerResponse.createByErrorMessage("该用户暂时没有登录权限");
        }
//
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
            int count = userMapper.insertSelective(user);
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

    /**
     * 检查所给的手机号是否已经注册
     * @param phoneNumber
     * @return
     */
    @Override
    public ServerResponse checkValid(String phoneNumber) {
        User user = null;
        user = userMapper.selectByPhoneNumber(phoneNumber);
        if (user == null) {
            return ServerResponse.createByErrorMessage("该手机号已经存在，请直接登录");
        }
        return ServerResponse.createBySuccessMessage("手机号未注册，请直接注册");
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse updateInformation(User user) {
        int count = 0;
        count = userMapper.updateByPrimaryKeySelective(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("更新个人信息失败");
        }
        return ServerResponse.createBySuccess("更新个人信息成功", user);
    }

    /**
     * 更新个人的密码， 这时用户在知道
     * @param userId  用户Id，主键
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    @Override
    public ServerResponse resetPassword(Integer userId, String oldPassword, String newPassword) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(oldPassword);
        int count = 0;
        count = userMapper.resetPassword(userId, oldPassword, newPassword);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    /**
     * 用户通过手机号+验证码的方式来修改密码。 这个方法不需要旧密码，只需要手机号就可以了
     * @param phoneNumber
     * @param newPassword
     * @return
     */
    @Override
    public ServerResponse forgetResetPassword(String phoneNumber, String newPassword) {
        int count = 0;
        count = userMapper.forgetResetPassword(phoneNumber, newPassword);
        if (count == 0) {
//            更新的行数为0，修改表失败，返回修改密码错误的信息
            return ServerResponse.createByErrorMessage("修改密码信息失败");
        }
        return ServerResponse.createBySuccessMessage("修改密码信息成功");
    }

    /**
     * 查询特定类型的用户类型的用户信息, 这里使用了分页插件PageHelper，使用逻辑如下
     *         //startPage--start
     *         //填充自己的sql查询逻辑
     *         //pageInfo-收尾
     * @param userType  要查询的用户的类型 0-普通用户， 1-商家， 2- 管理员
     * @param userStatus 要查询的用户的状态
     * @param pageNumber 要查询的页数
     * @param pageSize 每页的大小
     * @return
     */
    @Override
    public ServerResponse getUserList(Integer userType, Integer userStatus, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        List<User> userList = userMapper.getUserList(userType, userStatus);
        PageInfo pageInfo = new PageInfo(userList);
//        将查询结果封装成视图层对象
        List<UserVo> voList = assembleUserVoList(userList);
        pageInfo.setList(voList);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 更新用户的状态
     * @param userId  用户ID
     * @param userStatus 用户状态
     * @return
     */
    @Override
    public ServerResponse updateUserStatus(Integer userId, Integer userStatus) {
        int count = 0;
        User user = new User();
        user.setUserId(userId);
        user.setStatus(userStatus);
        count = userMapper.updateByPrimaryKeySelective(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("更新用户状态失败");
        }
        return ServerResponse.createBySuccessMessage("更新用户状态成功");
    }

    /**
     * 管理员通过此接口，完成增加新用户的功能
     * @param user
     * @return
     */
    @Override
    public ServerResponse addUser(User user) {
        String phoneNumber = user.getPhoneNumber();
        User temUser = userMapper.selectByPhoneNumber(phoneNumber);
        if (temUser == null) {
//            手机号不存在，可以添加
            int count = userMapper.insert(user);
            if(count == 0) {
                return ServerResponse.createByErrorMessage("添加用户失败");
            }
            return ServerResponse.createBySuccessMessage("添加用户成功");
        }
        return ServerResponse.createByErrorMessage("手机号已经存在，添加用户失败");
    }

    @Override
    public ServerResponse getUserByPhoneNumber(String phoneNumber) {
        User user = userMapper.selectByPhoneNumber(phoneNumber);
        if (user == null) {
            return ServerResponse.createByErrorMessage("查询用户信息失败");
        }
        return ServerResponse.createBySuccessData(assembleUserVo(user));
    }

    /**
     * 更新用户的头像
     * @param userId 要更新的用户的主键
     * @param file   图片文件
     * @param path   Tomcat本地暂时缓存图片文件的路径
     * @return
     */
    @Override
    public ServerResponse updatePhoto(Integer userId, MultipartFile file, String path) {
        String result = null;
        try {
            result = fileService.uploadFile(file, path,0);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("更新图片失败");
        }
        if (result == null) {
            return ServerResponse.createByErrorMessage("更新图片失败");
        }
//        上传图片成功，并且返回了图片的名称，在数据库中进行更新
        User user = new User();
        user.setUserId(userId);
        String filePath = PropertiesUtil.getProperty("ftp.server.ftp.prefix") + File.separator + result;
        user.setPhoto(filePath);
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
        return ServerResponse.createBySuccessMessage(filePath);
    }

    @Override
    public ServerResponse updatePhotoByUrl(Integer userId, String url) {
        User user = new User();
        user.setUserId(userId);
        user.setPhoto(url);
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("更新头像失败");
        }
        return ServerResponse.createBySuccessMessage(url);
    }


    /**
     * 将 User对象封装成UserVo对象的工具方法
     * @param users
     * @return
     */
    private List<UserVo> assembleUserVoList(List<User> users) {
        List<UserVo> voList = new LinkedList<>();
        if (users == null) {
            return voList;
        }
        for (User user : users) {
            voList.add(assembleUserVo(user));
        }
        return voList;
    }

    /**
     * 将 User对象封装成UserVo对象的工具方法
     * @param user
     * @return
     */
    private UserVo assembleUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setUserId(user.getUserId());
        userVo.setUserName(user.getUserName());
        userVo.setPhoneNumber(user.getPhoneNumber());
        userVo.setAccountBalance(user.getAccountBalance());
        userVo.setCreateTime(user.getCreateTime());
        userVo.setUpdateTime(user.getUpdateTime());
        userVo.setGender(user.getGender());
        userVo.setRole(user.getRole());
        userVo.setStatus(user.getStatus());
        return userVo;
    }

}

















