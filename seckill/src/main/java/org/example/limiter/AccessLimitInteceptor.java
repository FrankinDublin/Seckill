package org.example.limiter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.example.config.UserContext;
import org.example.domain.User;
import org.example.service.UserService;
import org.example.util.CookieUtil;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @description: 用拦截器实现限流
 * @author: Frankin
 * @create: 2022-02-19 14:56
 */
@Component
public class AccessLimitInteceptor implements HandlerInterceptor {
    @Autowired
    UserService service;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(o instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) o;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            User user = getUser(httpServletRequest,httpServletResponse);
            UserContext.setUser(user);
            if(accessLimit==null)
                return true;

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = httpServletRequest.getRequestURI();
            if(needLogin){
                if(user==null){
                    render(httpServletResponse,RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key+=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer integer = (Integer) valueOperations.get(key);
            if(integer==null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if(integer>maxCount){
                render(httpServletResponse,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }else {
                valueOperations.increment(key,1);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
    * @Description: 获取当前登录用户
    * @Param:
    * @return:
    */
    public User getUser(HttpServletRequest request,HttpServletResponse response){
        String ticket = CookieUtil.getCookie(request,"userTicket").getValue();
        if(StringUtils.isEmpty(ticket)) return null;
        return service.getUserByCookie(ticket,response,request);
    }

    /**
    * @Description: 处理异常
    * @Param:
    * @return:
    */
    private void render(HttpServletResponse response,RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }


}
