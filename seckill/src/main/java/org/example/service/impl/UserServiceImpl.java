package org.example.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.User;
import org.example.exception.GlobalException;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.example.util.CookieUtil;
import org.example.util.MD5Util;
import org.example.util.UUIDUtil;
import org.example.vo.LoginVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper mapper;
    @Autowired
    RedisTemplate redisTemplate;
    
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*if(isEmpty(mobile) || isEmpty(password)) return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        if(!ValidatorUtil.isMobile(mobile)) return RespBean.error(RespBeanEnum.MOBILE_ERROR);*/
        //根据手机号获取用户
        User user = mapper.getById(mobile);
        if(user == null) throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        //判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        String ticket = UUIDUtil.uuid();
        //request.getSession().setAttribute(ticket,user);
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user"+ticket,user);
        CookieUtil.addCookie(response,"userTicket",ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletResponse response, HttpServletRequest request) {
        if(StringUtils.isEmpty(userTicket)) return null;
        User u = (User) redisTemplate.opsForValue().get("user" + userTicket);
        if(u!=null){
            Cookie cookie = CookieUtil.getCookie(request, "userTicket");
            cookie.setValue(userTicket);
        }
        return u;
    }



}
