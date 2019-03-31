package com.wzyx.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.wzyx.common.Const;
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
import com.wzyx.util.DateTimeUtil;
import com.wzyx.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.io.InputStreamReader;
import java.util.*;

@Service("iOrderService")
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
     * 生成发订单
     * @param user
     * @param eventId
     * @param purchaseCount
     * @param eventSeason
     * @return
     */
    @Override
    public ServerResponse generate_order(User user, Integer eventId,Integer purchaseCount ,String eventSeason) {
        Order order = new Order();
        order.setoState(0);
        order.setUserId(user.getUserId());
        order.setpId(eventId);
       // Order o = ordermapper.seleect_by_userid_pid(user.getUserId(), eventId);
        Product product = productmapper.selectByPrimaryKey(eventId);
        String createtime =DateTimeUtil.dateToStr(new Date());
        String p_starttime = eventSeason;
         User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
        OrderVo orderVo = new OrderVo( user.getUserName(), user.getPhoneNumber(), shopUser.getUserName(), product.getpName()
                , product.getpPrice(), product.getpImage(), product.getpCate(),purchaseCount,null,createtime,product.getpLocation(),product.getpContent(),p_starttime);
        orderVo.setTotalprice(purchaseCount* product.getpPrice());

        //订单中加入商品数量和下单时的价格
        order.setTotalPrice(orderVo.getTotalprice());
        order.setpNumber(purchaseCount);
        order.setCreateTime(DateTimeUtil.strToDate(createtime));
        ordermapper.insertSelective(order);

        return ServerResponse.createBySuccessData(orderVo);
    }

    /**
     * 查看订单
     *
     * @param user       用户
     * @param oState     要查看的订单种类 0:待支付 1 待参与 -1已删除
     * @param pageNumber 页数
     * @param pageSize   每页有几条
     * @return
     */
    @Override
    public ServerResponse scan_order(User user, Integer oState, Integer pageNumber, Integer pageSize) {
        List<Order> od = ordermapper.selectBy_userId(user.getUserId(), oState);
        List<OrderVo> ov = new ArrayList<>();
        for (Order i : od) {
            Product product = productmapper.selectByPrimaryKey(i.getpId());
            String createtime =DateTimeUtil.dateToStr(i.getCreateTime());
            User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
            String paytime = DateTimeUtil.dateToStr(i.getoPaytime());
            String p_starttime = DateTimeUtil.dateToStr(product.getpStarttime());
            OrderVo orderVo = new OrderVo( user.getUserName(), user.getPhoneNumber(), shopUser.getUserName(), product.getpName()
                    , product.getpPrice(), product.getpImage(), product.getpCate(),i.getpNumber(),paytime,createtime,product.getpLocation(),product.getpContent(),p_starttime);
            orderVo.setTotalprice(i.getTotalPrice());
            ov.add(orderVo);
        }
        PageHelper.startPage(pageNumber, pageSize);
        PageInfo pageInfo = new PageInfo(ov);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 删除订单
     *
     * @param user 用户
     * @param oId  订单ID
     * @return
     */
    @Override
    public ServerResponse delete_order(User user, Integer oId) {
        Order order = ordermapper.selectByPrimaryKey(oId);
        order.setoState(-1);
        ordermapper.updateByPrimaryKeySelective(order);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    /**
     * 支付订单
     *
     * @param oId  订单ID
     * @param userId 用户ID
     * @return
     */
    @Override
    public ServerResponse pay_fake(Integer oId, Integer userId,int paymentMethod) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }

        if (order.getoState() == 1) {
            return ServerResponse.createByErrorMessage("订单已支付");
        }
        order.setoPaytime(new Date());
        order.setoState(1);
        order.setoPayway(paymentMethod);
        ordermapper.updateByPrimaryKeySelective(order);
        return ServerResponse.createBySuccessMessage("支付成功");

    }


    /**
     * 支付订单
     *
     * @param oId  订单ID
     * @param userId 用户ID
     * @return
     */
    @Override
    public ServerResponse pay(Integer oId, Integer userId) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }

        if(order.getoState()==1){
            return ServerResponse.createByErrorMessage("订单已支付");
        }


        //读取配置文件
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try (InputStreamReader inputStreamReader =new InputStreamReader(OrderService.class.getClassLoader().getResourceAsStream("zfbinfo.properties"),"UTF-8")) {
            properties.load(inputStreamReader);
        }catch (Exception e){
            System.out.println(e);
        }
        // 获取key对应的value值
        String serverUrl = properties.getProperty("open_api_domain");
        String appId = properties.getProperty("appid");
        String privateKey = properties.getProperty("private_key");
        String AlipayPublicKey = properties.getProperty("alipay_public_key");

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", AlipayPublicKey, "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("万众艺兴");
        model.setSubject("万众艺兴商品");
        model.setOutTradeNo(String.valueOf(order.getoId()));
        model.setTimeoutExpress("30m");
        model.setTotalAmount(String.valueOf(order.getTotalPrice()));
        model.setProductCode(String.valueOf(order.getpId()));
        Product product = productmapper.selectByPrimaryKey(order.getpId());
        model.setSellerId(String.valueOf(product.getUserId()));
        request.setBizModel(model);
        request.setNotifyUrl(properties.getProperty("call_back_url"));
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
            return ServerResponse.createBySuccess("支付宝支付信息",response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMessage("支付宝支付异常");
    }

    @Override
    public ServerResponse setOrderStatus(Integer oId, Integer userId,Integer status) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        Order o = new Order();
        o.setoId(order.getoId());
        o.setoState(status);
        int result = ordermapper.updateByPrimaryKeySelective(o);
        if(result>0){
            return ServerResponse.createBySuccessMessage("订单状态修改成功");
        }
        return ServerResponse.createByErrorMessage("订单状态修改失败");
    }

    @Override
    public ServerResponse order_detailed(User user, Integer oId) {
        Order o = ordermapper.selectByUserIdAndOrderNo(user.getUserId(),oId);
        Product product = productmapper.selectByPrimaryKey(o.getpId());
        User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
        String paytime = DateTimeUtil.dateToStr(o.getoPaytime());
        String createtime =DateTimeUtil.dateToStr(o.getCreateTime());
        String p_starttime = DateTimeUtil.dateToStr(product.getpStarttime());
        OrderVo orderVo = new OrderVo( user.getUserName(), user.getPhoneNumber(), shopUser.getUserName(), product.getpName()
                , product.getpPrice(), product.getpImage(), product.getpCate(), o.getpNumber(),paytime,createtime,product.getpLocation(),product.getpContent(),p_starttime);
        orderVo.setTotalprice(o.getTotalPrice());
        return ServerResponse.createBySuccessData(orderVo);
    }


    /**
     * 支付宝回调
     * @param params
     * @return
     */
    @Override
    public ServerResponse aliCallback(Map<String,String> params){
        Integer oId = Integer.parseInt(params.get("out_trade_no"));

        String tradeNo = params.get("trade_no");

        String tradeStatus = params.get("trade_status");
        Order order = ordermapper.selectByOrderNo(oId);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在,回调忽略");
        }
        //验证金额、订单号等信息
        if(order.getoState() == 1){
            return ServerResponse.createBySuccessMessage("订单已支付，支付宝重复调用");
        }
        if(order.getTotalPrice()!=Math.round(Double.valueOf((params.get("total_amount"))))){
            return ServerResponse.createByErrorMessage("支付金额错误");
        }
        if(order.getUserId()!=Integer.parseInt(params.get("seller_id"))){
            return ServerResponse.createByErrorMessage("商户不匹配");
        }

        if(order.getUserId()!=Integer.parseInt(params.get("seller_id"))){
            return ServerResponse.createByErrorMessage("商户不匹配");
        }

        //读取配置文件
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try (InputStreamReader inputStreamReader =new InputStreamReader(OrderService.class.getClassLoader().getResourceAsStream("zfbinfo.properties"),"UTF-8")) {
            properties.load(inputStreamReader);
        }catch (Exception e){
           e.printStackTrace();
        }

        String appId = properties.getProperty("appid");
        if(StringUtils.equals(appId,params.get("app_id"))){
            return ServerResponse.createByErrorMessage("app_id不匹配");
        }

        //验证成功
        if(com.wzyx.common.Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setoPaytime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setoState(1);
            order.setoPayway(1);
            ordermapper.updateByPrimaryKeySelective(order);
        }

        //支付详情页 可选 未实现
       /* PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);*/


       //TODO 支付详情页面

        return ServerResponse.createBySuccessMessage("支付成功");
    }


    /**
     * 获取订单支付状态
     * @param oId
     * @param userId
     * @return
     */
    @Override
    public ServerResponse orderStatus(Integer oId, Integer userId) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getoState() == Const.OrderStatus.ORDER_PAIED.getCode()) {
            return ServerResponse.createBySuccess(Const.OrderStatus.ORDER_PAIED.getDesc(), Const.OrderStatus.ORDER_PAIED.getCode());
        }
        if (order.getoState() == Const.OrderStatus.ORDER_UNPAIED.getCode()) {
            return ServerResponse.createBySuccess(Const.OrderStatus.ORDER_UNPAIED.getDesc(), Const.OrderStatus.ORDER_UNPAIED.getCode());
        }
        if (order.getoState() == Const.OrderStatus.ORDER_REFOUNDED.getCode()) {
            return ServerResponse.createBySuccess(Const.OrderStatus.ORDER_REFOUNDED.getDesc(), Const.OrderStatus.ORDER_REFOUNDED.getCode());
        }
        return ServerResponse.createBySuccess(Const.OrderStatus.ORDER_DELETED.getDesc(), Const.OrderStatus.ORDER_DELETED.getCode());
    }

    /**
     * 退款
     * @param oId
     * @param userId
     * @return
     */
    @Override
    public ServerResponse refound(Integer oId, Integer userId) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getoState() != Const.OrderStatus.ORDER_PAIED.getCode()) {
            return ServerResponse.createByErrorMessage("订单未支付或已删除");
        }
        //读取配置文件
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try (InputStreamReader inputStreamReader =new InputStreamReader(OrderService.class.getClassLoader().getResourceAsStream("zfbinfo.properties"),"UTF-8")) {
            properties.load(inputStreamReader);
        }catch (Exception e){
            System.out.println(e);
        }
        // 获取key对应的value值
        String serverUrl = properties.getProperty("open_api_domain");
        String appId = properties.getProperty("appid");
        String privateKey = properties.getProperty("private_key");
        String AlipayPublicKey = properties.getProperty("alipay_public_key");

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", AlipayPublicKey, "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.refund
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+order.getoId().toString()+"\"," +
                "    \"out_request_no\":\""+String.valueOf(order.getTotalPrice())+"\"," +
                "    \"refund_amount\":\""+String.valueOf(order.getTotalPrice())+"\"" +
                "  }");//设置业务参数
        //todo
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            System.out.print(response.getBody());
            Order o = new Order();
            o.setoId(order.getoId());
            o.setoState(Const.OrderStatus.ORDER_REFOUNDED.getCode());
            ordermapper.updateByPrimaryKeySelective(o);
            return ServerResponse.createBySuccessMessage("退款成功");
            //todo
        }catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return ServerResponse.createBySuccessMessage("退款失败");
    }
}
