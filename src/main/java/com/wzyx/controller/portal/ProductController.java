package com.wzyx.controller.portal;

import com.wzyx.common.ServerResponse;
import com.wzyx.common.enumration.ResponseCode;
import com.wzyx.common.enumration.Role;
import com.wzyx.pojo.Product;
import com.wzyx.pojo.User;
import com.wzyx.service.IProductService;
import com.wzyx.util.JsonUtil;
import com.wzyx.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService productservice;
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     *首页展示
     * @param pageNumber  几页
     * @param pageSize   每页有多少条目
     * @param p_cate 0 默认全部类别1 2 3 。。。不同类别
     * @param p_sort 排序方式 0：时间顺序 1：热度顺序
     * @param longgitude 维度
     * @param latitude   经度
     * @param distance   距离
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public ServerResponse show_list(  @RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
            ,@RequestParam(value = "p_cate",defaultValue = "0") Integer  p_cate
            ,@RequestParam(value = "p_sort",defaultValue = "0") Integer p_sort
            ,@RequestParam(value = "longgitude",defaultValue = "100") Double longgitude
            ,@RequestParam(value = "latitude",defaultValue = "100") Double latitude
            ,@RequestParam(value = "distance",defaultValue = "5000") Integer distance){

        return productservice.show_list(pageNumber,pageSize,p_cate,p_sort,longgitude,latitude,distance);
    }

    /**
     *
     * @param request
     * @param authToken 用户的redis key判断是否登录
     * @param p  商品
     * @param file 封面缩略图
     * @param files 详细图片
     * @return
     */
    @RequestMapping("issue")
    @ResponseBody
    public ServerResponse issue_product(HttpServletRequest request, String authToken, Product p, @RequestParam(value = "file") MultipartFile file,
                                        @RequestParam(value = "files") MultipartFile[] files) {
        String userString = RedisPoolUtil.get(authToken);
        if (userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString, User.class);
        if (user.getRole() != Role.SELLER.getCode())
            return ServerResponse.createByErrorMessage("用户权限不够");
        else {
            if (!file.isEmpty()&&files.length!=0) {
                String path = request.getServletContext().getRealPath("img");
                return productservice.issue_product(user, p, path, file,files);
            }
        }
        return  ServerResponse.createByErrorMessage("需要图片信息");
    }

    /**
     * 商家更新商品
     * @param request
     * @param authToken 用户的redis key判断是否登录
     * @param p 更新的商品
     * @param file 改变的缩略图
     * @param files 增加的详细图片
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ServerResponse update_product(HttpServletRequest request, String authToken, Product p, @RequestParam(value = "file", required = false) MultipartFile file,
                                     @RequestParam(value = "files",required = false)   MultipartFile[] files) {
        String userString = RedisPoolUtil.get(authToken);
        if (userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString, User.class);
        if (user.getRole() != Role.SELLER.getCode())
            return ServerResponse.createByErrorMessage("用户权限不够");
        else {
            String path = request.getServletContext().getRealPath("img");
            return productservice.update_product(user, p,path,file,files);
        }
    }
    /**
     * 商家查看产品信息
     * @param authToken 用户的redis key判断是否登录
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("shopscan")
    @ResponseBody
    public ServerResponse shop_scan(String authToken, @RequestParam(value = "pStatte", defaultValue = "1") Integer pState,@RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                    @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return productservice.shop_scan(user.getUserId(),pState,pageNumber,pageSize);
    }

    /**
     *
     * @param pId  商品Id
     * @return
     */
    @RequestMapping("detailed")
    @ResponseBody
    public ServerResponse product_detailed(Integer pId){
        return productservice.scan_product_detailed(pId);
    }
    /**
     * 商家下架商品,应该不存在下架不是该商家发布的商品，所以不做判断
     * @param authToken
     * @param pId
     * @return
     */
    @RequestMapping("drop")
    @ResponseBody
    public ServerResponse drop_product(String authToken,@RequestParam(value = "pId") Integer pId){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        if(user.getRole()!= Role.SELLER.getCode())
            return ServerResponse.createByErrorMessage("用户权限不够") ;
        else
            return productservice.drop_product(pId);
}

    /**
     * 用户根据名称搜索活动
     * @param pName  输入的名字
     * @param pageNumber
     * @param pageSize
     * @return
     */

    @RequestMapping("search_byname")
    @ResponseBody
    public
    ServerResponse search_by_name(@RequestParam(value = "pName") String pName,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                                     @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        if(pName==null||pName=="")
        return ServerResponse.createByErrorMessage("输入信息不能为空");
        else
            return productservice.search_by_name(pName,pageNumber,pageSize);
}

    /**
     * 用户搜索附近的活动 暂时不需要
     * @param longgitude
     * @param latitude
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public ServerResponse search_by_nearby(@RequestParam(value = "longgitude") double longgitude,@RequestParam(value = "latitude") double latitude
                                       ,@RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber
                                       ,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
                                        ,@RequestParam(value = "distance",defaultValue = "5") Integer distance){
        return
                productservice.search_by_nearby(longgitude,latitude,pageNumber,pageSize,distance);
}

    /**暂时不需要 需要一个商品列表的参数？
     * 应该处于同一个页面中的不同栏目
     *
     *  按时间排序
     * @return
     */
    @RequestMapping(value = "sortby_date")
    @ResponseBody
    public ServerResponse sortby_date(  @RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                    @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                    List<Product> list){
    return productservice.sortby_date(pageNumber,pageSize,list);
    }

    /**暂时不需要
     * 按参加热度排序
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "sortby_hot")
    @ResponseBody
    public ServerResponse sortby_hot(@RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                 @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                 List<Product> list){
        return productservice.sortby_hot(pageNumber,pageSize,list);
}
    /**
     *
     * 用户添加产品
     * @param authToken  用户标签
     * @param pId   商品id
     * @param numbers 添加的数量 默认为1
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ServerResponse add_product(String authToken,Integer pId
            ,@RequestParam(value = "numbers",defaultValue = "1") Integer numbers){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return productservice.add_product(user,pId,numbers);
    }

    /**
     *
     * 用户查看自己参加的产品
     * @param authToken  用户标记
     * @param pageNumber 分页数
     * @param pageSize   每页的大小
     * @return
     */
    @RequestMapping("scan_product")
    @ResponseBody
    public ServerResponse scan_product(String authToken,
                                       @RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                                       @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String userString = RedisPoolUtil.get(authToken);
        if(userString == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        User user = JsonUtil.str2Object(userString,User.class);
        return productservice.scan_product(user,pageNumber,pageSize);
    }

    /**
     * 用户删掉自己参加的产品
     * @param authToken
     * @param pId
     * @return
     */
    @RequestMapping(value = "delete_product")
    @ResponseBody
    public ServerResponse delete_product(String authToken,Integer pId
            ,@RequestParam(value = "numbers",defaultValue = "1") Integer numbers){
    String userString = RedisPoolUtil.get(authToken);
    if(userString == null)
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    User user = JsonUtil.str2Object(userString,User.class);
   return productservice.delete_product(user,pId,numbers);
}

}
