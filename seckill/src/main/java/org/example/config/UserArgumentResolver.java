package org.example.config;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.User;
import org.example.service.UserService;
import org.example.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 自定义用户配置参数
 * @author: Frankin
 * @create: 2022-02-12 12:44
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    UserService service;
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class clazz = methodParameter.getParameterType();
        return clazz == User.class;     //结果为true时启用下面的解析

    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request =  nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response =  nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookie(request,"userTicket").getValue();
        if(StringUtils.isEmpty(ticket)) return null;
        return service.getUserByCookie(ticket,response,request);
    }
}
