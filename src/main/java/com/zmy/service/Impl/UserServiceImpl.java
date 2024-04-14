package com.zmy.service.Impl;

import com.zmy.dao.UserInfoDao;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @author zmy
 * @create 2024-04-14 16:06
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoDao userInfoDao;
    @Override
    public User getUser(String id) {
        return userInfoDao.getUserInfoById(id);
    }

    @Override
    public void addUser(String name, int age, String birthday) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setAge(age);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            user.setBirthDay(simpleDateFormat.parse(birthday));
        }catch (Exception e){
            e.printStackTrace();
        }
        userInfoDao.addUser(user);
    }
}
