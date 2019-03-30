package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import com.wzyx.pojo.User;

import java.util.Map;

public interface IOrderService {
    ServerResponse generate_order(User user, Integer eventId,Integer purchaseCount ,String eventSeason);

    ServerResponse scan_order(User user,Integer oState, Integer pageNumber, Integer pageSize);

    ServerResponse delete_order(User user, Integer oId);

    ServerResponse pay(Integer oId, Integer userId);

    ServerResponse pay_fake(Integer oId, Integer userId,int paymentMethod);

    ServerResponse aliCallback(Map<String,String> params);

    ServerResponse refound(Integer oId, Integer userId);

    ServerResponse orderStatus(Integer oId, Integer userId);

    ServerResponse setOrderStatus(Integer oId, Integer userId,Integer status);


    ServerResponse order_detailed(User user, Integer oId);
}
