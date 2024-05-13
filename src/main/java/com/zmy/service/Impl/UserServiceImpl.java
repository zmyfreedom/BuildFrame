package com.zmy.service.Impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.Pictures;
import com.zmy.Exception.CustomException;
import com.zmy.dao.UserInfoDao;
import com.zmy.entity.User;
import com.zmy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    @Override
    public void generateDoc(HttpServletResponse response){
        Map<String, Object> map = getMap();
        //获取根目录，创建模板文件
        String filePath = copyTempFile("public/word/DocTemplate.docx");
        String fileName=System.currentTimeMillis()+".docx";
        String tempPath="E:\\Java"+fileName;
        try{
            //将模板文件写入到根目录
            //编译模板，渲染数据
            XWPFTemplate template = XWPFTemplate.compile(filePath).render(map);
            //将渲染后的模板写入到临时文件
            FileOutputStream fos=new FileOutputStream(tempPath);
            template.write(fos);
            fos.flush();
            fos.close();
            template.close();
            //将临时文件写入到response
            downDoc(response,tempPath,fileName);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //删除临时文件
            File file = new File(tempPath);
            if(file.exists()){
                file.delete();
            }
            File file2 = new File(filePath);
            if(file2.exists()){
                file2.delete();
            }
        }
    }

    private String copyTempFile(String tempFilePath){
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(tempFilePath);
        String tempFileName = System.getProperty("user.home")+"/GenerateDoc.docx";
        File file = new File(tempFileName);
        try{
            FileUtils.copyInputStreamToFile(inputStream,file);
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return file.getPath();
    }

    private void downDoc(HttpServletResponse response,String filePath, String realFileName){
        String percentEncodedFileName=null;
        try{
            percentEncodedFileName=percentEncode(realFileName);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private String percentEncode(String fileName)throws UnsupportedEncodingException {
        String encode= URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
    private Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","CodeGeeX");
        map.put("role","AI编程助手");
        map.put("work","帮助用户在运行时访问和打印静态常量，从而避免编译时的潜在问题");
        map.put("age",10000);
        map.put("date",new Date());
        map.put("food","apple");
        map.put("image", Pictures.ofLocal("E:/Java/code/src/main/resources/static/icecream.png").size(100,50).create());
        return map;
    }
}
