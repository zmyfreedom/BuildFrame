package com.zmy.controller;

import com.zmy.common.ResponseResult;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

/**
 * @author zmy
 * @create 2024-04-14 14:36
 */
@RestController
@RequestMapping("user")
@Tag(name="用户管理")
@Slf4j
public class UserController {
//    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @GetMapping("getUser")
    @Operation(summary = "通过id获取用户信息")
    public ResponseResult<User> getUser(@Param("id")@RequestParam String id){
        try{
            log.info("info logTest,id:{}", id);
            log.debug("debug  logTest,id:{}", id);
            User user=userService.getUser(id);
            log.debug(user.toString());
            return ResponseResult.ok(user);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error(400,null,"查询失败");
        }
    }

    @PostMapping("addUser")
    @Operation(summary = "添加用户")
    public ResponseResult<String> addUser(@Param("id") @RequestParam String id, @Param("name") @RequestParam String name,
                                          @Param("age") @RequestParam int age, @Param("birthDay") @RequestParam String birthDay){
        try{
            userService.addUser(id,name,age,birthDay);
            return ResponseResult.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error(400,null,"添加失败");
        }
    }
}
