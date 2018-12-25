package com.wzyx.dao;

import com.wzyx.pojo.Product;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer pId);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer pId);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product scanproduct(Integer pid);

    void updatestate(Integer pid);

    List<Product> shop_scan(@Param("uid") Integer uid, @Param("pState") Integer pState);

    List<Product> search_by_name(@Param("name") String name);

    Product selectByPrimaryKey(@Param("pid") Integer pid, @Param("userid") Integer userid);

    List<Product> selectall();

    List<Product> slectallby_p_cate(Integer p_cate);

    List<Product> selectby_lalo(@Param("longgitude") double longgitude,@Param("latitude") double latitude,@Param("distance") Integer distance);
}
