package org.example.service.impl;

import org.example.domain.User;
import org.example.exception.GlobalException;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.example.util.MD5Util;
import org.example.util.ValidatorUtil;
import org.example.vo.LoginVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper mapper;
    @Override
    public RespBean doLogin(LoginVo loginVo) {
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
        return RespBean.success(RespBeanEnum.SUCCESS);
    }
}
