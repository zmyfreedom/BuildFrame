package com.zmy.service.Impl;

import com.zmy.Exception.CustomException;
import com.zmy.dao.UserInfoDao;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @author zmy
 * @create 2024-04-14 16:06
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public User getUser(String id) {
        //通过 RequestContextHolder.getRequestAttributes() 获取当前请求的属性对象
        //RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //通过 resolveReference(RequestAttributes.REFERENCE_REQUEST) 方法从属性对象中解析出 HttpServletRequest 对象
        //HttpServletRequest request1 = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //log.info("request1:{}",request1);

        /*通过 RequestContextHolder.getRequestAttributes() 获取当前请求的属性对象,转换为 ServletRequestAttributes 类型*/
        //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //通过 getRequest() 方法获取 HttpServletRequest 对象
        //HttpServletRequest request2 = attributes.getRequest();
        //log.info("request2: {}",request2);

        //currentRequestAttributes call getRequestAttributes actually
        HttpServletRequest request3 = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("request3: {}",request3);

        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        log.info("response: {}",response);
        log.info("contentType:{}",response.getContentType());
        response.setContentType("application/json;charset=UTF-8");

        User user=userInfoDao.getUserInfoById(id);

        try{
            log.info("user.id: {}",user.getId());
            //int i = 10/0;
            //return user;
        }catch(CustomException e){
            e.printStackTrace();
            throw e;
        }catch(NullPointerException e){
            e.printStackTrace();
            log.info("impl npe:{}",user.getId());
            throw e;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
            //throw new CustomException(CustomExceptionEnum.PARAMETER_BIG_EXCEPTION);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)//
    public void addUser(String id, String name, int age, String birthday) throws Exception {
        User user = new User();
        if(id == null || id.equals(""))
            user.setId(UUID.randomUUID().toString());
        else
            user.setId(id);
        user.setName(name);
        user.setAge(age);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            user.setBirthDay(simpleDateFormat.parse(birthday));
            System.out.println(simpleDateFormat.parse("1998-12-20"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        System.out.println(user);
        userInfoDao.addUser(user);
        //在service层抛出异常，事务才会回滚，如果事务被本地的try catch处理了，则不会回滚
        //事务的范围比锁的范围大,解锁后事务可能还未退出，此时数据库尚未解锁
        //
        try{
            throw new RuntimeException("addUser exception");
        }catch(Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException("addUser exception");
    }
}
