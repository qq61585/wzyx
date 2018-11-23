package com.wzyx.dao;

import com.wzyx.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User login(@Param("phoneNumber") String phoneNumber,@Param("password") String password);

    User selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    int resetPassword(@Param("userId") Integer userId, @Param("oldPassword") String oldPassword,
                      @Param("newPassword") String newPassword);

    int forgetResetPassword(@Param("phoneNumber") String phoneNumber, @Param("newPassword") String newPassword);

    List<User> getUserList(@Param("userType") Integer userType, @Param("userStatus") Integer userStatus);
}