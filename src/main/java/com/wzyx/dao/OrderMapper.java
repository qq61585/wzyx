package com.wzyx.dao;

import com.wzyx.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer oId);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer oId);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectBy_userId(@Param("userId") Integer userId,@Param("oState") Integer oState);

    Order seleect_by_userid_pid(@Param("userId") Integer userId, @Param("pId") Integer pId);
}