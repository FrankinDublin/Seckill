package org.example.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @description: 商品
 * @author: Frankin
 * @create: 2022-02-11 13:36
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @RequestMapping("/toList")
    public String toList(HttpSession session, Model model, HttpServletRequest request){
        /**
        * @Description: 跳转商品列表页
        * @Param: [session, model, ticket]
        * @return: java.lang.String
        */
        String ticket = showCookies(request,"userTicket");
        System.out.println("Controller ticket:"+ticket);
        if(StringUtils.isEmpty(ticket)) return "login";
        User user = (User) session.getAttribute(ticket);
        System.out.println(user);
        if(user == null) return "login";
        model.addAttribute("user",user);
        return "goodsList";
    }
    public String showCookies(HttpServletRequest request,String name){
        Cookie[] cookies = request.getCookies();//根据请求数据，找到cookie数组

        if (null==cookies) {//如果没有cookie数组
            System.out.println("没有cookie");
        } else {
            for(Cookie cookie : cookies){
                //System.out.println(cookie.getName());
                if(name.equals(cookie.getName())) return cookie.getValue();
            }
        }
        return "error";
    }
}
