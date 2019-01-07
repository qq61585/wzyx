package com.wzyx.common;

public class Const {
    public enum OrderStatus {

        ORDER_PAIED(1,"订单已支付"),
        ORDER_UNPAIED(0,"订单未支付"),
        ORDER_DELETED(-1,"订单已删除"),
        ORDER_REFOUNDED(-2,"订单已退款");

        private int code;
        private  String desc;

        OrderStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }


    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
}




