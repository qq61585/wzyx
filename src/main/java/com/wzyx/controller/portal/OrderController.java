package com.wzyx.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.wzyx.common.Const;
import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.pojo.User;
import com.wzyx.service.impl.OrderService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.RedisPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Transactional
@Controller
@RequestMapping(value = "/order/")
public class OrderController {
    @Autowired
    private OrderService orderService;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);
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
@RequestMapping("/order_detailed")
@ResponseBody
public ServerResponse order_detailed(String authToken,Integer oId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.order_detailed(user,oId);
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




    //支付

    /**
     * 订单支付
     *
     * @param authToken  用户的redis key判断是否登录
     * @param oId   要支付的订单ID
     * @return
     */
    @RequestMapping(value = "pay_order")
    @ResponseBody
    public ServerResponse pay(String authToken, Integer oId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.pay(oId,user.getUserId());
    }


    @RequestMapping(value = "set_order_status")
    @ResponseBody
    public ServerResponse setOrderStatus(String authToken, Integer oId,Integer status){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.setOrderStatus(oId,user.getUserId(),status);
    }

    /**
     * 支付宝回调信息，判断是否支付成功
     *
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){

        Configs.init("zfbinfo.properties");

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        params.remove("sign");
        params.remove("sign_type");
        try {

            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }


        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        ServerResponse serverResponse = orderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }


    /**
     * 获取订单支付状态
     * @param authToken
     * @param oId
     * @return
     */
    @RequestMapping(value = "order_status")
    @ResponseBody
    public ServerResponse orderStatus(String authToken, Integer oId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.orderStatus(oId,user.getUserId());
    }



    /**
     * 支付宝退款
     * @param authToken  用户的redis key判断是否登录
     * @param oId   要退款的订单ID
     * @param refoundAmount 退款金额
     * @return
     */

    @RequestMapping("refound")
    @ResponseBody
    public ServerResponse refound(String authToken, Integer oId,Double refoundAmount){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        User user = JsonUtil.str2Object(userString,User.class);
        return orderService.refound(oId,user.getUserId(),refoundAmount);
    }
}
