package org.example.config;

import org.example.limiter.AccessLimitInteceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @description: mvc配置类，作用：改变SpringMVC的Controller传入参数，实现可以User替换Token做为参数从登陆页面传到商品列表页面
 * @author: Frankin
 * @create: 2022-02-12 12:41
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    UserArgumentResolver userArgumentResolver;
    @Autowired
    AccessLimitInteceptor accessLimitInteceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
        list.add(userArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInteceptor);
    }
}
