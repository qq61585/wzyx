package com.wzyx.dao;

import com.wzyx.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User login(@Param("phoneNumber") String phoneNumber,@Param("password") String password);

    User selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}