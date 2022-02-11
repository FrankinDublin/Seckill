package org.example.service;


import org.example.vo.LoginVo;
import org.example.vo.RespBean;

public interface UserService {
    RespBean doLogin(LoginVo loginVo);
}
