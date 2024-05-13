package com.zmy.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author zmy
 * @create 2024-04-22 21:23
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public GroupedOpenApi userApi() {      // 创建了一个api接口的分组
        return GroupedOpenApi.builder()
                .group("UserManager")         // 分组名称
                .pathsToMatch("/user/**")  // 接口请求路径规则
                .build();
    }
    @Bean
    public GroupedOpenApi resourceApi() {      // 创建了一个api接口的分组
        return GroupedOpenApi.builder()
                .group("ResourceManager")         // 分组名称
                .pathsToMatch("/listener/**")  // 接口请求路径规则
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("标题")
                        .contact(new Contact().name("作者"))
                        .description("我的API文档")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                        .externalDocs(new ExternalDocumentation()
                        .description("外部文档")
                        .url("https://springshop.wiki.github.org/docs"));
    }


}
