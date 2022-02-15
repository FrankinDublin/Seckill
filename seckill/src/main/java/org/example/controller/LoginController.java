package org.example.controller;


import org.example.service.UserService;
import org.example.vo.LoginVo;
import org.example.vo.RespBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")

public class LoginController {
    @Autowired
    UserService service;
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @ResponseBody
    @RequestMapping("/do_login")
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletResponse response, HttpServletRequest request){
        /**
        * @Description: 接收前端的信息，根据信息查找对应用户信息返回给前端
        * @Param: [loginVo：电话和密码]
        * @return: org.example.vo.RespBean：查找到的用户信息
        */
        log.info(loginVo.toString());
        RespBean respBean = service.doLogin(loginVo,response,request);
        System.out.println(respBean.getObj());
        log.info(respBean.toString());
        return respBean;
    }
}
