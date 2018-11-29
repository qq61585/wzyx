package com.wzyx.service;

import com.wzyx.common.ServerResponse;
import com.wzyx.pojo.User;
import org.springframework.web.multipart.MultipartFile;


public interface IUserService {

    ServerResponse login(String phoneNumber, String password, Integer role);

    ServerResponse register(String phoneNumber, String password, int role, String verification);

    ServerResponse checkValid(String phoneNumber);

    ServerResponse updateInformation(User user);

    ServerResponse resetPassword(Integer userId, String oldPassword, String newPassword);


    ServerResponse forgetResetPassword(String phoneNumber, String newPassword);

    ServerResponse getUserList(Integer userType, Integer useStatus, Integer pageNumber, Integer pageSize);

    ServerResponse updateUserStatus(Integer userId, Integer userStatus);


    ServerResponse addUser(User user);

    ServerResponse getUserByPhoneNumber(String phoneNumber);

    ServerResponse updatePhoto(Integer userId, MultipartFile file, String path);


    ServerResponse updatePhotoByUrl(Integer userId, String url);
}