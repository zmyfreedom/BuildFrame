package com.zmy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.Pictures;
import com.zmy.Exception.CustomException;
import com.zmy.dao.BaseDao;
import com.zmy.dao.UserInfoDao;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * @author zmy
 * @create 2024-04-14 16:06
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private ApplicationContext applicationContext;

    // 日期格式化器 - 线程安全且高效
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // 共享线程池 - 避免每次创建销毁线程池的开销
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            2, 
            10, 
            60L,
            TimeUnit.SECONDS, 
            new LinkedBlockingDeque<>(100),
            new BasicThreadFactory.Builder().namingPattern("addUser-thread-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    @Override
    public User getUser(String id) {
        // 使用 RequestContextHolder.currentRequestAttributes() 获取当前请求属性
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("request: {}",request);

        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        log.info("response: {}",response);
        log.info("contentType:{}",response.getContentType());
        response.setContentType("application/json;charset=UTF-8");

        User user=userInfoDao.getUserInfoById(id);

        try{
            log.info("user.id: {}",user.getId());

        }catch(CustomException e){
            log.error("CustomException occurred", e);
            throw e;
        }catch(NullPointerException e){
            log.error("NPE when accessing user.id, user is null", e);
            throw e;
        }catch(Exception e){
            log.error("Unexpected error occurred", e);
        }
        return user;
    }

    // Transactional 将整个方法放入事务中，如果方法内部抛出异常，则事务会回滚
    // 注意：如果事务被本地的 try-catch 处理了，则不会回滚
    // 内部多线程不在事务之内，如果线程抛出异常，则不会回滚
    // 事务更新到数据库需要一定时间，线程立即读是读不到的
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(String id, String name, int age, String birthday) throws Exception {
        User user = new User();

        if(StrUtil.isEmpty(id)) {
            user.setId(IdUtil.randomUUID());
        } else {
            user.setId(id);
        }
        user.setName(name);
        user.setAge(age);
        
        // 使用 Java 8 的日期时间 API，线程安全且高效
        try{
            LocalDate localDate = LocalDate.parse(birthday, DATE_FORMATTER);
            user.setBirthday(java.sql.Date.valueOf(localDate));
        } catch (Exception e){
            log.error("Invalid date format: {}", birthday, e);
            throw e;
        }

        log.info("Creating user: {}", user);
        
        //在 service 层抛出异常，事务才会回滚，如果事务被本地的 try-catch 处理了，则不会回滚
        //事务的范围比锁的范围大，解锁后事务可能还未退出，此时数据库尚未解锁
        try{
            insert(user, UserInfoDao.class);
            
            // 使用共享线程池，避免每次创建销毁线程池的开销
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Thread interrupted", e);
                    return;
                }
                User user1 = userInfoDao.getUserInfoById(id);
                log.info("addUser async check\n{}",user1);
                // 注意：异步线程中的异常不会影响主事务
            });
        }catch(Exception e){
            log.error("addUser error:{}",e.getMessage(), e);
            throw e;
        }
    }

    private <E, D extends BaseDao<E>> void insert(E user, Class<D> clazz){
        D obj = applicationContext.getBean(clazz);
        obj.add(user);
    }
    
    @Override
    public void generateDoc(HttpServletResponse response){
        Map<String, Object> map = getMap();
        //获取根目录，创建模板文件
        String filePath = copyTempFile("classpath:public/word/DocTemplate.docx");
        String fileName=System.currentTimeMillis()+".docx";
        String tempPath="E:\\\\Java"+fileName;
        try (XWPFTemplate template = XWPFTemplate.compile(filePath).render(map);
             FileOutputStream fos = new FileOutputStream(tempPath)) {
            // 编译模板，渲染数据并写入临时文件
            template.write(fos);
            fos.flush();
            // 将临时文件写入到 response
            downDoc(response, tempPath, fileName);
        } catch (Exception e) {
            log.error("Failed to generate document", e);
        } finally {
            // 删除临时文件
            deleteFileIfExists(tempPath);
            deleteFileIfExists(filePath);
        }
    }

    private String copyTempFile(String tempFilePath) {
        String tempFileName = System.getProperty("user.home") + "/GenerateDoc.docx";
        File file = new File(tempFileName);
        try (InputStream inputStream = new FileInputStream(new File("E:\\\\Java\\\\code\\\\src\\\\main\\\\resources\\\\public\\\\word\\\\DocTemplate.docx"))) {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            log.error("Failed to copy template file", e);
            throw new RuntimeException(e);
        }
        return file.getPath();
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (!file.delete()) {
                    log.warn("Failed to delete file: {}", filePath);
                }
            }
        }
    }

    private void downDoc(HttpServletResponse response,String filePath, String realFileName){
        String percentEncodedFileName=null;
        try{
            percentEncodedFileName=percentEncode(realFileName);
        }catch (UnsupportedEncodingException e){
            log.error("Failed to encode filename", e);
            throw new RuntimeException(e);
        }

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment;filename=").append(percentEncodedFileName)
                .append(";").append("filename*=").append("utf-8''").append(percentEncodedFileName);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-Disposition",contentDispositionValue.toString());
        response.setHeader("download-filename",percentEncodedFileName);

        try(BufferedInputStream bis=new BufferedInputStream(new FileInputStream(filePath));
            BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());){
            byte[] buffer = new byte[1024];
            int len=0;
            while((len=bis.read(buffer))!=-1){
                bos.write(buffer,0,len);
            }
        }catch (IOException e){
            log.error("Failed to write response", e);
        }
    }

    private String percentEncode(String fileName)throws UnsupportedEncodingException {
        String encode= URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
    
    private Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>(7);
        map.put("name","CodeGeeX");
        map.put("role","AI 编程助手");
        map.put("work","帮助用户在运行时访问和打印静态常量，从而避免编译时的潜在问题");
        map.put("age",10000);
        map.put("date",new Date());
        map.put("food","apple");
        map.put("image", Pictures.ofLocal("E:/Java/code/src/main/resources/static/icecream.png").size(100,50).create());
        return map;
    }
}
