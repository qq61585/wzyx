package com.wzyx.service;

import com.wzyx.common.ServerResponse;


public interface IUserService {

    ServerResponse login(String phoneNumber, String password);

    ServerResponse register(String phoneNumber, String password, int role, String verification);
}