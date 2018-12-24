package com.wzyx.controller.portal;

import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.pojo.User;
import com.wzyx.service.impl.OrderService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Transactional
@Controller
@RequestMapping(value = "/order/")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 生成订单
     * @param authToken 用户的redis key判断是否登录
     * @param pId 商品ID
     * @return
     */
    @RequestMapping(value = "gen_order")
    @ResponseBody
    public ServerResponse generate_order(String authToken, Integer pId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return  orderService.generate_order(user,pId);
    }

    /**
     * 查看订单
     * @param authToken 用户的redis key判断是否登录
     * @param oState    订单种类 要查看的订单种类 0:待支付 1 已支付 -1已删除
     * @param pageNumber 页数
     * @param pageSize  每页有几条
     * @return
     */
    @RequestMapping(value = "scan_order")
    @ResponseBody
    public ServerResponse scan_order(String authToken,Integer oState,
                                     @RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                     @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.scan_order(user,oState,pageNumber,pageSize);
    }

    /**
     * 删除订单
     * @param authToken  用户的redis key判断是否登录
     * @param oId  订单ID
     * @return
     */
    @RequestMapping("delete_order")
    @ResponseBody
    public ServerResponse delete_order(String authToken,Integer oId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.delete_order(user,oId);
    }
}
