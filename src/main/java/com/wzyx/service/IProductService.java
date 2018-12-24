package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import com.wzyx.pojo.Product;
import com.wzyx.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    public ServerResponse scan_product(User user,Integer pageNumber,Integer pageSize);
    public ServerResponse add_product(User user,Integer pid,Integer numbers);
    public ServerResponse issue_product(User user, Product product, String path, MultipartFile file, MultipartFile[] files);
    public ServerResponse update_product(User u, Product product, String path, MultipartFile file, MultipartFile[] files);
    public ServerResponse scan_product_detailed(Integer pid);
    public ServerResponse drop_product(Integer pid);
    public ServerResponse shop_scan(Integer uid,Integer pState,Integer pageNumber,Integer pageSize);
    public ServerResponse search_by_name(String name,Integer pageNumber,Integer pageSize);
    public ServerResponse search_by_nearby(double longgitude,double latitude,Integer pageNumber,Integer pageSize,Integer distance);
    public ServerResponse sortby_date(Integer pageNumber, Integer pageSize, List<Product> list);

    public ServerResponse sortby_hot(Integer pageNumber, Integer pageSize,List<Product> list);

    public ServerResponse delete_product(User user, Integer pid,Integer numbers);

    ServerResponse show_list(Integer pageNumber, Integer pageSize, Integer p_cate, Integer p_sort,double longgitude,double latitude,Integer distance);

}
