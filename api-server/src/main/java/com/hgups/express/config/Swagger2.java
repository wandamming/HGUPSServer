package com.hgups.express.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Value("${server.servlet.context-path}")
    private String urlPrefix;

    @Value("${request.host}")
    private String requestHost;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hgups.express.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        String responseDemo = "{\n" +
                "    \"statusCode\": 200,\n" +
                "    \"msg\": \"SUCCESS\"\n" +
                "  \"data\": {xxx:xxx}\n" +
                "}";

        String desc = "1、调用接口时（除注册登陆接口外）需在http请求header中添加access-token参数，登陆接口可获取access-token的值；" +
                "\n\r 2、请求的 Base URL：\n" + requestHost + urlPrefix +
                "\n\r 3、返回模板:\n" +
                "\n" + responseDemo +
                "\n\r statusCode表示返回的状态码(自定义的，和http状态码区分开),200为请求成功，msg字段为与状态码对应的文字描述信息，data字段为具体的返回数据其他状态码请查询相关文档";
        return new ApiInfoBuilder()
                .title("HGUPS 国际快运系统 API文档")
                .description(desc)
                .termsOfServiceUrl("http://www.baidu.com/")
                .version("1.0")
                .build();
    }
}
