package com.wzyx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzyx.common.ServerResponse;
import com.wzyx.dao.OrderMapper;
import com.wzyx.dao.ProductMapper;
import com.wzyx.dao.ShoppingcartMapper;
import com.wzyx.dao.UserMapper;
import com.wzyx.pojo.Order;
import com.wzyx.pojo.Product;
import com.wzyx.pojo.Shoppingcart;
import com.wzyx.pojo.User;
import com.wzyx.service.IOrderService;
import com.wzyx.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingcartMapper shoppingcart;
    @Autowired
    private ProductMapper productmapper;
    @Autowired
    private OrderMapper ordermapper;

    /**
     * 生成订单
     * @param user 用户
     * @param pId 商品ID
     * @return
     */
    @Override
    public ServerResponse generate_order(User user, Integer pId) {
        Order order = new Order();
        order.setoState(0);
        order.setUserId(user.getUserId());
        order.setpId(pId);
        ordermapper.insertSelective(order);
        Order o = ordermapper.seleect_by_userid_pid(user.getUserId(),pId);
        Product product = productmapper.selectByPrimaryKey(pId);
        Shoppingcart sc = shoppingcart.selectby_uid_and_pid(user.getUserId(),pId);
        User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
        OrderVo orderVo = new OrderVo(o.getoId(),user.getUserName(),user.getPhoneNumber(),shopUser.getUserName(),product.getpName()
                ,product.getpPrice(),product.getpImage(),product.getpCate(),sc.getsNumber());
        orderVo.setTotalprice(sc.getsNumber()*product.getpPrice());
        return ServerResponse.createBySuccessData(orderVo);
    }

    /**
     * 查看订单
     * @param user 用户
     * @param oState 要查看的订单种类 0:待支付 1 已支付 -1已删除
     * @param pageNumber 页数
     * @param pageSize 每页有几条
     * @return
     */
    @Override
    public ServerResponse scan_order(User user, Integer oState,Integer pageNumber, Integer pageSize) {
        List<Order> od = ordermapper.selectBy_userId(user.getUserId(),oState);
        PageHelper.startPage(pageNumber, pageSize);
        List<OrderVo> ov = new ArrayList<>();
        for (Order i : od) {
            Product product = productmapper.selectByPrimaryKey(i.getpId());
            Shoppingcart sc = shoppingcart.selectby_uid_and_pid(user.getUserId(), i.getpId());
            User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
            OrderVo orderVo = new OrderVo(i.getoId(),user.getUserName(),user.getPhoneNumber(),shopUser.getUserName(),product.getpName()
                    ,product.getpPrice(),product.getpImage(),product.getpCate(),sc.getsNumber());
            orderVo.setTotalprice(sc.getsNumber()*product.getpPrice());
            ov.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo(ov);
        return  ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 删除订单
     * @param user 用户
     * @param oId 订单ID
     * @return
     */
    @Override
    public ServerResponse delete_order(User user, Integer oId) {
        Order order = ordermapper.selectByPrimaryKey(oId);
        order.setoState(-1);
        ordermapper.updateByPrimaryKeySelective(order);
        return ServerResponse.createByErrorMessage("删除成功");
    }
}
