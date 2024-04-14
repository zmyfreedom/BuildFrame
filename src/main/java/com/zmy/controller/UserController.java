package com.zmy.controller;

import com.zmy.common.ResponseResult;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;


/**
 * @author zmy
 * @create 2024-04-14 14:36
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;


    @GetMapping("getUser")
    public ResponseResult<User> getUser(@Param("id")String id){
        try{
            return ResponseResult.ok(userService.getUser(id));
        }catch (Exception e){
            return ResponseResult.error(400,null,"error");
        }
    }

    @PostMapping("addUser")
    public ResponseResult<String> addUser(@Param("name")String name, @Param("age") int age, @Param("birthDay") String birthDay){
        try{
            userService.addUser(name,age,birthDay);
            return ResponseResult.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error(400,null,"添加失败");
        }
    }
}
