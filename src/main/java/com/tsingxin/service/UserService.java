package com.tsingxin.service;

import com.tsingxin.error.BusinessException;
import com.tsingxin.service.model.UserModel;

public interface UserService {
    //通过用户id 获取用户对象的方法
    UserModel getUserById(Integer id);

    //通过缓存获取用户对象
    UserModel getUserByIdInCache(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /*
    number: 用户注册学号
    password: 用户加密后密码
    */
    UserModel validateLogin(String number, String encrptPassword) throws BusinessException;
}
