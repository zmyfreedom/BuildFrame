package com.zmy.common;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MapperHotSwap {
    @Autowired
    private MybatisProperties mybatisProperties;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private Resource[] mapperLocations;
    private Configuration config;
    private HashMap<String, Long> fileChange = new HashMap<>();

    @org.springframework.context.annotation.Configuration
    @ConfigurationProperties(prefix = MapperHotSwapProperties.PREFIX)
    @Data
    public static class MapperHotSwapProperties {
        public static final String PREFIX = "mybatis.mapper";

        private Boolean reload = false;
    }

    @Autowired
    private MapperHotSwapProperties hotSwapProperties;

    @PostConstruct
    public void init() {
        try{
            if(!hotSwapProperties.getReload())
                return;
            prepareEnv();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    changeCompare();
                }
            };
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(runnable, 1, 10, TimeUnit.SECONDS);
            log.info("Mapper热更新已启动");
        }catch (Exception e){
            log.error("Mapper热更新启动失败", e);
        }
    }
    // 初始化配置
    public void prepareEnv() throws Exception {
        this.config = sqlSessionFactory.getConfiguration();
        this.mapperLocations = new PathMatchingResourcePatternResolver()
                .getResources(mybatisProperties.getMapperLocations()[0]);

        for (Resource mapperLocation : mapperLocations) {
            //文件内容帧值
            long lastFrame = mapperLocation.contentLength() + mapperLocation.lastModified();
            fileChange.put(mapperLocation.getFilename(), Long.valueOf(lastFrame));
        }
    }

    // 比较文件内容帧值
    public void changeCompare() {
        try{
            if(!isChanged())
                return;
            //清理
            removeConfig(config);
            //重新加载
            for (Resource mapperLocation : mapperLocations){
                try{
                    XMLMapperBuilder builder = new XMLMapperBuilder(
                            mapperLocation.getInputStream(), config, mapperLocation.toString(), config.getSqlFragments());
                    builder.parse();
                }catch (IOException e){
                    log.error("mapper文件{}不存在或内部错误",mapperLocation.getFilename());
                }
            }
            log.info("Mapper文件已更新");
        }catch (Exception e){
            log.error("Mapper文件更新失败:{}", e.getMessage());
        }
    }

    // 判断文件是否被修改
    public boolean isChanged() {
        boolean flag = false;
        try {
            for (Resource mapperLocation : mapperLocations) {
                String resourceName = mapperLocation.getFilename();
                Long lastFrame = fileChange.get(resourceName);
                long currentFrame = mapperLocation.contentLength() + mapperLocation.lastModified();
                fileChange.put(resourceName, currentFrame);
                //新增还是修改，保存文件最新帧
                boolean addFlag = !fileChange.containsKey(resourceName);
                boolean modifyFlag = null != lastFrame && !lastFrame.equals(currentFrame);
                if (addFlag || modifyFlag) {
                    flag = true;
                    log.info("文件{}被修改", resourceName);
                }
            }
        } catch (IOException e) {
            log.error("文件{}读取失败", e.getMessage());
        }
        return flag;
    }
    // 移除配置
    private void removeConfig(Configuration configuration) throws Exception{
        Class<?> classConfig = configuration.getClass();
        clearMap(classConfig,configuration,"mappedStatements");
        clearMap(classConfig,configuration,"caches");
        clearMap(classConfig,configuration,"resultMaps");
        clearMap(classConfig,configuration,"parameterMaps");
        clearMap(classConfig,configuration,"keyGenerators");
        clearMap(classConfig,configuration,"sqlFragments");
        // 如果使用的是Mybatis Plus，Mybatis Plus 使用的配置类是 Configuration 的子类 MybatisConfiguration。
        // 要去其父类 Configuration 中找 loadedResources 这个属性
        // 这里使用的是mybatis，不需要找 Configuration 的子类 MybatisConfiguration
        clearSet(classConfig, configuration,"loadedResources");
    }

    private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName){
        Field field = getDeclaredField(classConfig, fieldName);
        if(Objects.isNull(field))
            return;
        field.setAccessible(true);
        Map mapConfig = getFieldValue(field, configuration);
        if (Objects.nonNull(mapConfig))
            mapConfig.clear();
    }

    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName){
        Field field = getDeclaredField(classConfig, fieldName);
        if(Objects.isNull(field))
            return;
        field.setAccessible(true);
        Set setConfig = getFieldValue(field, configuration);
        if (Objects.nonNull(setConfig))
            setConfig.clear();
    }

    private <T> T getFieldValue(Field field, Object obj){
        T value = null;
        try{
            value = (T) field.get(obj);
        }catch (IllegalAccessException e){
        }
        return value;
    }
    private Field getDeclaredField(Class<?> classConfig, String fieldName){
        Field field = null;
        try{
            field = classConfig.getDeclaredField(fieldName);
        }catch (NoSuchFieldException e){
        }
        return field;
    }
}
