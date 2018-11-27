package com.wzyx.dao;

import com.wzyx.pojo.SellerMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellerMaterialMapper {
    int deleteByPrimaryKey(Integer sellerMaterialId);

    int insert(SellerMaterial record);

    int insertSelective(SellerMaterial record);

    SellerMaterial selectByPrimaryKey(Integer sellerMaterialId);

    int updateByPrimaryKeySelective(SellerMaterial record);

    int updateByPrimaryKey(SellerMaterial record);

    List<SellerMaterial> selectBySellerId(@Param("sellerId") String sellerId);
}