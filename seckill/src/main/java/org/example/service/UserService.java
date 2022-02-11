package org.example.service;


import org.example.vo.LoginVo;
import org.example.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request);
}
