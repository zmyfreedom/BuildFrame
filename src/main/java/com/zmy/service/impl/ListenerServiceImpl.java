package com.zmy.service.impl;

import com.zmy.entity.User;
import com.zmy.event.MyEvent;
import com.zmy.service.ListenerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ListenerServiceImpl implements ListenerService{
    @Resource
    ApplicationContext applicationContext;
    @Override
    public User getUser() {
        return new User("4","貂蝉",20,new Date());
    }

    @Override
    public User getUserEvent(){
        User user=new User("5","吕布",30,new Date());
        MyEvent event = new MyEvent(this, user);
        // 发布事件, 事件会自动被监听器(MyEventListener)处理
        applicationContext.publishEvent(event);
        return user;
    }
}


