package com.wzyx.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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
     * 生成订单
     *
     * @param user 用户
     * @param pId  商品ID
     * @return
     */
    @Override
    public ServerResponse generate_order(User user, Integer pId) {
        Order order = new Order();
        order.setoState(0);
        order.setUserId(user.getUserId());
        order.setpId(pId);
        Order o = ordermapper.seleect_by_userid_pid(user.getUserId(), pId);
        Product product = productmapper.selectByPrimaryKey(pId);
        Shoppingcart sc = shoppingcart.selectby_uid_and_pid(user.getUserId(), pId);
        User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
        OrderVo orderVo = new OrderVo(o.getoId(), user.getUserName(), user.getPhoneNumber(), shopUser.getUserName(), product.getpName()
                , product.getpPrice(), product.getpImage(), product.getpCate(), sc.getsNumber());
        orderVo.setTotalprice(sc.getsNumber() * product.getpPrice());

        //订单中加入商品数量和下单时的价格
        order.setTotalPrice(orderVo.getTotalprice());
        order.setpNumber(sc.getsNumber());
        ordermapper.insertSelective(order);

        return ServerResponse.createBySuccessData(orderVo);
    }

    /**
     * 查看订单
     *
     * @param user       用户
     * @param oState     要查看的订单种类 0:待支付 1 已支付 -1已删除
     * @param pageNumber 页数
     * @param pageSize   每页有几条
     * @return
     */
    @Override
    public ServerResponse scan_order(User user, Integer oState, Integer pageNumber, Integer pageSize) {
        List<Order> od = ordermapper.selectBy_userId(user.getUserId(), oState);
        PageHelper.startPage(pageNumber, pageSize);
        List<OrderVo> ov = new ArrayList<>();
        for (Order i : od) {
            Product product = productmapper.selectByPrimaryKey(i.getpId());
            Shoppingcart sc = shoppingcart.selectby_uid_and_pid(user.getUserId(), i.getpId());
            User shopUser = userMapper.selectByPrimaryKey(product.getUserId());
            OrderVo orderVo = new OrderVo(i.getoId(), user.getUserName(), user.getPhoneNumber(), shopUser.getUserName(), product.getpName()
                    , product.getpPrice(), product.getpImage(), product.getpCate(), sc.getsNumber());
            orderVo.setTotalprice(sc.getsNumber() * product.getpPrice());
            ov.add(orderVo);
        }
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
    public ServerResponse pay(Integer oId, Integer userId) {

        Order order = ordermapper.selectByUserIdAndOrderNo(userId, oId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
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
    public ServerResponse aliCallback(Map<String,String> params){
        Integer oId = Integer.parseInt(params.get("out_trade_no"));

        String tradeNo = params.get("trade_no");

        String tradeStatus = params.get("trade_status");
        Order order = ordermapper.selectByOrderNo(oId);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在,回调忽略");
        }
        if(order.getoState() == 1){
            return ServerResponse.createBySuccessMessage("订单已支付，支付宝重复调用");
        }
        if(com.wzyx.common.Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setoPaytime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setoState(1);
            order.setoPayway(1);
            ordermapper.updateByPrimaryKeySelective(order);
        }

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


    public static void main(String[] args) {
        // 获取key对应的value值
        Configs.init("zfbinfo.properties");
        String serverUrl = Configs.getOpenApiDomain();
        String appId = Configs.getAppid();
        String privateKey = Configs.getPrivateKey();
        String AlipayPublicKey = Configs.getAlipayPublicKey();

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", AlipayPublicKey, "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("万众艺兴");
        model.setSubject("万众艺兴商品");
        model.setOutTradeNo("1523");
        model.setTimeoutExpress("30m");
        model.setTotalAmount("45");
        model.setProductCode("45");
        request.setBizModel(model);
        request.setNotifyUrl("properties.getProperty(call_back_url)");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

}
