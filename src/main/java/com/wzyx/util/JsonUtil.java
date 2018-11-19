package com.wzyx.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Json序列化的工具类，通过它实现User对象和Json字符串之间的相互转换，
 * Redis数据库中存储的数据机构， 键为authToken，值为User对象Json序列化后的字符串
 */
public class JsonUtil {

    private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();//实现序列化的工具类

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 将任意对象转化成json字符串
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String  obj2String (T obj) {
        if (obj == null) {
            return null;
        }
        String string = null;
        try {
//            如果传进来的对象就是字符串，就不做转换，否则就调用函数完成转换
            string = (obj instanceof String) ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to json fails", e);
        }
        return string;
    }

    /**
     * 实现把一个json字符串转化成对应的对象的功能，需要传入目标对象的Class对象
     * 这个方法只能实现简单Class对象的转换，对 Map<String, User>这类无法完成转换
     * @param string
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T str2Object(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string) || clazz == null) {
//            传入的字符串为空、 或者类对象为null 直接返回。StringUtils为判断string是否为空的工具类
            return null;
        }
        try {
//            如果要转换的类型是String ，那么就直接返回json字符串就可以了，否则就调用函数完成转换
            return (String.class.equals(clazz)) ? (T)string : objectMapper.readValue(string, clazz);
        } catch (IOException e) {
            log.warn("Parse string to object fails", e);
            return null;
        }
    }

    /**
     * 这个方法也是实现json字符串到Java对象的转换，使用TypeReference类辅助，
     * 可以实现json字符串到复杂类型对象的转换，例如 Map<String, User>
     *      * 示例如下：
     * JsonUtil.str2Object(string, new TypeReference<Map<String, User>>(){});
     * TypeReference 是一个辅助类，调用时只需要声明对应的泛型就可以了，不用实现任何方法
     * @param string
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T str2Object(String string, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(string) || typeReference == null) {
//            传进来的字符串为空、或者要转换的目标对象的模板为null，直接返回
            return null;
        }
        try {
//            如果要转换的目标对象是String类型，那么直接返回string就可以了，否则调用方法
            return (typeReference.getType().equals(String.class)) ? (T)string : objectMapper.readValue(string, typeReference);
        } catch (IOException e) {
            log.warn("Parse string to Object fails", e);
            return null;
        }

    }


}













