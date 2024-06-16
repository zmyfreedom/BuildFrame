package com.zmy.dao;

import com.zmy.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zmy
 * @create 2024-04-14 16:08
 */
//在启动类上使用@MapperScan("com.zmy.dao"),则在其他dao上不用再单独使用@Mapper
@Mapper
public interface UserInfoDao {
    User getUserInfoById(String  id);

    void addUser(User user);
}
