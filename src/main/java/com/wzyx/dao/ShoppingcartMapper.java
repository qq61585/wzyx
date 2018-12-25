package com.wzyx.dao;

import com.wzyx.pojo.Shoppingcart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingcartMapper {
    int deleteByPrimaryKey(Integer sId);

    int insert(Shoppingcart record);

    int insertSelective(Shoppingcart record);

    Shoppingcart selectByPrimaryKey(Integer sId);

    int updateByPrimaryKeySelective(Shoppingcart record);

    int updateByPrimaryKey(Shoppingcart record);

    Shoppingcart selectby_uid_and_pid(@Param("userId") Integer userId,@Param("pid") Integer pid);

    void update_delete_product(@Param("userId") Integer userId, @Param("pid") Integer pid,@Param("numbers") Integer numbers);

    List<Shoppingcart> selectByUserId(Integer uId);
}