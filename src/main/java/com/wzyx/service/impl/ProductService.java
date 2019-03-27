package com.wzyx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzyx.common.CalculateTwoPosition;
import com.wzyx.common.ServerResponse;
import com.wzyx.dao.OrderMapper;
import com.wzyx.dao.ProductMapper;
import com.wzyx.dao.ShoppingcartMapper;
import com.wzyx.dao.UserMapper;
import com.wzyx.pojo.Product;
import com.wzyx.pojo.Shoppingcart;
import com.wzyx.pojo.User;
import com.wzyx.service.IProductService;
import com.wzyx.util.PropertiesUtil;
import com.wzyx.vo.ProductVo;
import com.wzyx.vo.ShoppingcartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
@Service("iProductService")
@Transactional
public class ProductService implements IProductService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingcartMapper shoppingcart;
    @Autowired
    private ProductMapper productmapper;
    @Autowired
    private OrderMapper ordermapper;
    @Autowired
    private FileService fileservice;
    /**
     * 首页展示
     * @param pageNumber
     * @param pageSize
     * @param p_cate
     * @param p_sort
     * @param longgitude
     * @param latitude
     * @param distance
     * @return
     */
    @Override
    public ServerResponse show_list(Integer pageNumber, Integer pageSize, Integer p_cate, Integer p_sort,double longgitude,double latitude,Integer distance) {
        List<Product> list = new ArrayList<>();
        if(p_cate==0)
            list = productmapper.selectall();
        else
            list = productmapper.slectallby_p_cate(p_cate);
        if(latitude!=100)//如果没勾选设定维度值为100
            list = productmapper.selectby_lalo(longgitude,latitude,distance);
        PageHelper.startPage(pageNumber, pageSize);
        PageInfo pageInfo = new PageInfo();
        if (p_sort == 0) //按时间顺序排序
        {
            list.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if (o1.getpStarttime().before(o2.getpStarttime()))
                        return 1;
                    else
                        return -1;
                }
            });
        } else if (p_sort == 1) { //按参加热度排序
            list.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if (o1.getpHasadded() / o1.getpCapacity() < o2.getpHasadded() / o2.getpCapacity())
                        return 1;
                    else
                        return -1;
                }
            });
        }
        List<ProductVo>  productVoList = new ArrayList<ProductVo>();
        for(Product p:list){
            String t = p.getpImagelist();
            String[] save  = t.split("#");
            List<String> imagelist = Arrays.asList(save);
            ProductVo productVo = new ProductVo(p);
            productVo.setpImagelist(imagelist);
            productVoList.add(productVo);
        }
        pageInfo.setList(productVoList);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 商家发布产品
     * @param user
     * @param product
     * @param path
     * @param file  首页图
     * @param files 详细图
     * @return
     */
    @Override
    public ServerResponse issue_product(User user, Product product,String path, MultipartFile file,MultipartFile[] files) {
        String filename = fileservice.uploadFile(file,path,1);
        String imagename = PropertiesUtil.getProperty("ftp.server.ftp.test");
        String p_image = imagename+ File.separator+filename;
        String[] p_imagelist = fileservice.uploadFiles(files,path,1);
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i<p_imagelist.length;i++) {
            sb.append(imagename);
            sb.append(File.separator);
            sb.append(p_imagelist[i]);
            sb.append("#");
        }
        product.setpImagelist(sb.toString());
        product.setpImage(p_image);
        product.setUserId(user.getUserId());
        product.setpState(1);
        productmapper.insertSelective(product);
        return ServerResponse.createBySuccessMessage("上架成功");
    }

    /**
     * 商家更新产品
     * @param u
     * @param product
     * @param path
     * @param file
     * @param files
     * @return
     */
    @Override
    public ServerResponse update_product(User u,Product product,String path,MultipartFile file,MultipartFile[] files) {
        String imagename = PropertiesUtil.getProperty("ftp.server.ftp.test");
        if(file==null&&files.length==0) {//删除图片信息或者没有改变，url在传过来是已有改变
         //   productmapper.updateByPrimaryKeySelective(product);
            int i =1;
        }
        else if(file!=null&&files.length!=0)
        {
            String filename = fileservice.uploadFile(file,path,1);
            String p_image = imagename+ File.separator+filename;
            String[] p_imagelist = fileservice.uploadFiles(files,path,1);
            StringBuffer sb = new StringBuffer();
            for(int i = 0;i<p_imagelist.length;i++) {
                sb.append(imagename);
                sb.append(File.separator);
                sb.append(p_imagelist[i]);
                sb.append("#");
            }
            product.setpImagelist(product.getpImagelist()+sb.toString());//添加了图片信息
            product.setpImage(p_image);
        }
        else if(file==null&&files.length!=0){
            String[] p_imagelist = fileservice.uploadFiles(files,path,1);
            StringBuffer sb = new StringBuffer();
            for(int i = 0;i<p_imagelist.length;i++) {
                sb.append(imagename);
                sb.append(File.separator);
                sb.append(p_imagelist[i]);
                sb.append("#");
            }
            product.setpImagelist(product.getpImagelist()+sb.toString());//添加了图片信息
        }
        else{
            String filename = fileservice.uploadFile(file,path,1);
            String p_image = imagename+ File.separator+filename;
            product.setpImage(p_image);

        }
        productmapper.updateByPrimaryKeySelective(product);
        return ServerResponse.createBySuccessMessage("更新成功");
    }

    /**
     * 查看商品的详细信息
     * @param pid
     * @return
     */
    @Override
    public ServerResponse scan_product_detailed(Integer pid) {
        Product p= productmapper.selectByPrimaryKey(pid);
        String temp = p.getpImagelist();
        String[] r = temp.split("#");
        List<String> list = Arrays.asList(r);
        ProductVo pv = new ProductVo(p);
        pv.setpImagelist(list);
        return ServerResponse.createBySuccessData(pv);
    }

    /**
     * 商家下架产品
     * @param pid
     * @return
     */
    @Override
    public ServerResponse drop_product(Integer pid) {
    productmapper.updatestate(pid);
    return ServerResponse.createBySuccessMessage("下架成功");
    }

    /**
     * 商家查看商品信息
     * @param uid
     * @param pState
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse shop_scan(Integer uid,Integer pState, Integer pageNumber, Integer pageSize) {
        List<Product> products = productmapper.shop_scan(uid,pState);
        PageHelper.startPage(pageNumber,pageSize);
        PageInfo page = new PageInfo(products);
        return ServerResponse.createBySuccessData(page);
    }

    /**
     * 用户查看自己参加的产品
     * @param user
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse scan_product(User user, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        List<Shoppingcart>  scart= shoppingcart.selectByUserId(user.getUserId());
        User user1 = userMapper.selectByPrimaryKey(user.getUserId());
        List<ShoppingcartVo> list = new ArrayList<>();
        for (Shoppingcart i:scart){
            int pid = i.getpId();
            Product product = productmapper.selectByPrimaryKey(pid);
            ShoppingcartVo shoppingcartVo = new ShoppingcartVo(product.getpId(),product.getpName(),user1.getUserName(),i.getsNumber(),product.getpImage(), product.getpPrice()*i.getsNumber());
            list.add(shoppingcartVo);
        }
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 用户添加产品
     * @param user
     * @param pid
     * @param numbers  添加的数量 默认为1
     * @return
     */
    @Override
    public ServerResponse add_product(User user, Integer pid,Integer numbers) {
        Product product = productmapper.selectByPrimaryKey(pid);
        if(product.getpCapacity()-product.getpHasadded()<=0)
            return ServerResponse.createByErrorMessage("库存不足");
        else {
            Shoppingcart o = shoppingcart.selectby_uid_and_pid(user.getUserId(), pid);
            if (o == null) {//没有记录
                Shoppingcart sp = new Shoppingcart();
                sp.setpId(pid);
                sp.setUserId(user.getUserId());
                sp.setsNumber(numbers);
                shoppingcart.insertSelective(sp);
            } else {
                int num = o.getsNumber();
                o.setsNumber(num+numbers);
                shoppingcart.updateByPrimaryKey(o);
            }
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }

    /**
     * 用户根据名称搜索活动
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse search_by_name(String name, Integer pageNumber, Integer pageSize) {
        List<Product> list= productmapper.search_by_name(name);
        PageHelper.startPage(pageNumber,pageSize);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    @Override
    public ServerResponse search_by_nearby(double longgitude, double latitude, Integer pageNumber, Integer pageSize, Integer distance) {
       PageHelper.startPage(pageNumber,pageSize);
        List<Product> list = productmapper.selectall();
        List<Product> r = new ArrayList<>();

        for(int i = 0;i<list.size();i++){
            if(CalculateTwoPosition.getDistance(latitude,longgitude,list.get(i).getpLatitude(),list.get(i).getpLonggitude())<=distance)
                r.add(list.get(i));
        }
        PageInfo pageInfo = new PageInfo(r);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    @Override
    public ServerResponse sortby_date(Integer pageNumber, Integer pageSize,List<Product> list) {
        list.sort(new Comparator<Product>(){
            @Override
            public int compare(Product o1, Product o2) {
                    if(o1.getpStarttime().before(o2.getCreateTime()))
                        return -1;
                    else
                        return 1;
            }
        });
        PageHelper.startPage(pageNumber,pageSize);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    @Override
    public ServerResponse sortby_hot(Integer pageNumber, Integer pageSize,List<Product> list) {
        //List<Product> list = productmapper.slectall();//找到还处于上架过程中的产品
        list.sort(new Comparator<Product>(){
            @Override
            public int compare(Product o1, Product o2) {
                if(o1.getpHasadded()/o1.getpCapacity()<o2.getpHasadded()/o2.getpCapacity())
                    return -1;
                else
                    return 1;
            }
        });
        PageHelper.startPage(pageNumber,pageSize);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    /**
     * 用户删除产品
     * @param user
     * @param pid
     * @param numbers 人数 默认为1
     * @return
     */
    @Override
    public ServerResponse delete_product(User user, Integer pid,Integer numbers) {
        Product product = productmapper.selectByPrimaryKey(pid);
        Shoppingcart sp = shoppingcart.selectby_uid_and_pid(user.getUserId(),pid);
        if(sp.getsNumber()<numbers)
          sp.setsNumber(numbers);
        shoppingcart.updateByPrimaryKeySelective(sp);
        if(numbers>product.getpCapacity())
            return ServerResponse.createByErrorMessage("人数非法");
        shoppingcart.update_delete_product(user.getUserId(),pid,numbers);
        return ServerResponse.createBySuccessMessage("删除成功");
    }


}
