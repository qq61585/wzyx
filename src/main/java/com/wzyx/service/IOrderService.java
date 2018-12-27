package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import com.wzyx.pojo.User;

public interface IOrderService {
    ServerResponse generate_order(User user, Integer pId);

    ServerResponse scan_order(User user,Integer oState, Integer pageNumber, Integer pageSize);

    ServerResponse delete_order(User user, Integer oId);

    ServerResponse pay(Integer oId, Integer userId);
}
