package org.example.service;


import org.example.domain.User;
import org.example.vo.LoginVo;
import org.example.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request);

    
    User getUserByCookie(String userTicket, HttpServletResponse response, HttpServletRequest request);
    /**
    * @Description: 根据userTicket获取用户
    * @Param: 
    * @return: 
    */ 
}
