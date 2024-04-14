package com.zmy.service;

import com.zmy.entity.User;

/**
 * @author zmy
 * @create 2024-04-14 16:04
 */
public interface UserService {
    User getUser(String id);

    void addUser(String name, int age, String birthDay);
}
