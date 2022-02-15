package org.example.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.User;
import org.example.service.GoodsService;
import org.example.service.UserService;
import org.example.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 优化前：1100
 * @description: 商品
 * @author: Frankin
 * @create: 2022-02-11 13:36
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @RequestMapping("/toList")
    public String toList(Model model,User user){
        /**
        * @Description: 跳转商品列表页
        * @Param: [session, model, ticket]
        * @return: java.lang.String
        */
        /*if(StringUtils.isEmpty(ticket)) return "login";
        //User user = (User) session.getAttribute(ticket);
        User user = userService.getUserByCookie(ticket, response, request);     //判断逻辑放入参数解析器中了
        System.out.println("user==null:"+user);
        if(user == null) return "login";*/
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        return "goods_list";
    }

    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId){

        model.addAttribute("user",user);

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int seckillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            //未开始
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if(nowDate.after(endDate)){
            //已结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else{
            //进行中
            seckillStatus = 1;
        }
        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }

}
