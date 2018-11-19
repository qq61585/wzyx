package com.wzyx.common;



import com.wzyx.common.enumration.ResponseCode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;


/**
 * 一个高复用的相应类，用来向前端返回json数据，有 status， msg， data三个主要字段
 * status 响应状态，0-成功， 1- 失败， 2- 需要登录 3- 输入的参数非法
 * msg 对应的字符串信息
 * data 里面对应具体的返回对象的信息
 * 例如：
 *  status": 0,
 * "msg": "用户注册成功",
 * "data":{
 *      "userId": 54,
 *      "userName": null,
 *      "password": null,
 *      "photo": null,
 *      "age": null,
 *      "gender": null,
 *      "phoneNumber": "11111111",
 *      "qqNumber": null,
 *      "wechatNumber": null,
 *      "email": null,
 *      "totalPoints": null,
 *      "accountBalance": null,
 *      "createTime": null,
 *      "updateTime": null,
 *      "role": 0,
 *      "status": 1
 *      }
 * }
 */
// 在序列化json的时候，如果属性是null，则不进行序列化
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    //后端逻辑需要的方法，不需要进行序列化
    @JsonIgnore
    public boolean isSuccess() {
        return status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 返回相应成功，msg为默认值
     * @param <T>
     * @return
     */
    public  static <T> ServerResponse<T> creatBySuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc());
    }

    /**
     * 返回响应成功，msg为指定值
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    /**
     * 返回响应成功，data对象为指定值
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccessData(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    /**
     * 返回响应成功，msg对象和 data对象都为指定值
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 返回响应错误，msg信息为默认值
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    /**
     * 返回响应错误， msg为指定值
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByErrorMessage(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg);
    }

    /**
     * 返回响应错误，data对象为指定值
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByErrorData(T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), data);
    }

    /**
     *返回响应错误，msg，data对象为指定值
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError(String msg, T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg, data);
    }

    /**
     *返回相应信息，响应码和msg均为指定值
     * @param errorCode
     * @param msg
     * @param <T>
     * @return
     */
    public static <T>ServerResponse<T> createByErrorCodeMessage(int errorCode, String msg) {
        return new ServerResponse<>(errorCode, msg);
    }

}








