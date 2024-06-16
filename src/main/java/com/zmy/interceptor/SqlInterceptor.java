package com.zmy.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class SqlInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 计算sql语句执行时间
        long start = System.currentTimeMillis();
        Object proceed = invocation.proceed();
        long end = System.currentTimeMillis();

        String printSql = null;
        try {
            // 通过generateSql方法拿到最终生成的sql
            printSql = generateSql(invocation);
        } catch (Exception e) {
            log.error("获取sql异常", e);
        } finally {
            // 拼接打印日志过程
            long cost = end - start;
            log.info("\n执行sql:{}\n耗时:{}ms", printSql, cost);
        }
        return proceed;
    }

    //Invocation对象是MyBatis执行SQL调用时的上下文
    private String generateSql(Invocation invocation) throws Exception {
        // 获取到BoundSql以及Configuration对象
        // BoundSql对象存储了一条具体的SQL语句以及相关参数信息
        // Configuration对象存储了MyBatis的配置信息，例如mapper映射文件中的参数信息
        // MappedStatement对象存储了mapper映射文件中的SQL语句信息
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        Configuration configuration = statement.getConfiguration();
        BoundSql boundSql = statement.getBoundSql(parameter);

        // 获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 获取参数映射
        List<ParameterMapping> params = boundSql.getParameterMappings();
        // 获取sql语句
        String sql = boundSql.getSql();
        // SQL中多个空格使用一个空格代替
        sql = sql.replaceAll("[\\s]+", " ");
        if (!ObjectUtils.isEmpty(params) && !ObjectUtils.isEmpty(parameterObject)) {
            // TypeHandlerRegistry是mybatis用来管理TypeHandler的注册器，用于在java类型和jdbc类型之间进行转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果参数对象的类型有对应的TypeHandler，则直接使用TypeHandler进行转换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                // 否则遍历参数映射，获取参数名称和参数值，进行替换
                for (ParameterMapping param : params) {
                    // 获取参数属性名
                    String propertyName = param.getProperty();
                    // 获取参数属性值
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    // 检查对象中是否存在该属性的getter方法，如果存在就取出来替换
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //检查boundSql中是否存在附加参数，附加参数可能是在动态sql中生成的
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 如果都没有，说明sql匹配不上，带上’缺失’方便查找
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }

    private String getParameterValue(Object obj) {
        String value = "";
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else if (!ObjectUtils.isEmpty(obj)) {
            value = obj.toString();
        }
        return value;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以通过properties配置插件参数
    }
}
