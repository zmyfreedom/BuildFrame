package com.zmy.dao;

import com.zmy.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zmy
 * @create 2024-04-14 16:08
 */
@Mapper
public interface UserInfoDao {
    User getUserInfoById(String  id);

    void addUser(User user);
}
