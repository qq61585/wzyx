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

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("oId") Integer oId);

    Order selectByOrderNo(Integer oId);

    void updateallproduct();//更新待评价  原状态为1且 已超时   更新为2

    void updateallproduct1();//更新已过期  原装状态为0 时间也超时 更新为 3

    void updateallproduct2(); //更新已完成   原状态为2 且时间也超时  更新为7

}