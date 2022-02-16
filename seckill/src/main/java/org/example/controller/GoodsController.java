package org.example.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.User;
import org.example.service.GoodsService;
import org.example.service.UserService;
import org.example.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 优化前：1100
 * 缓存后：1842
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
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;    //用于手动渲染页面
    //@RequestMapping("/toList")
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody   //将页面跳转切换为返回页面（已缓存）的方式
    public String toList(Model model,User user,HttpServletResponse response,HttpServletRequest request){
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

        //从redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String goodsList = (String) valueOperations.get("goodsList");   //缓存的html一定是string类型的
        if(!StringUtils.isEmpty(goodsList)) return goodsList;
        //没有的话就手动渲染页面并放入redis
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        goodsList = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);    //手动渲染
        if(!StringUtils.isEmpty(goodsList)){
            valueOperations.set("goodsList",goodsList,60, TimeUnit.SECONDS);
        }
        return goodsList;
    }

    //@RequestMapping("/toDetail/{goodsId}")
    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId,HttpServletResponse response,HttpServletRequest request){
        //先从redis中获取
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String goodsDetail = (String) valueOperations.get("goodsDetail:"+goodsId);
        if(!StringUtils.isEmpty(goodsDetail)) return goodsDetail;
        //获取不到时手动渲染并存入redis
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

        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        goodsDetail = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",webContext);
        if(!StringUtils.isEmpty(goodsDetail)){
            valueOperations.set("goodsDetail:"+goodsId,goodsDetail,60,TimeUnit.SECONDS);
        }

        return goodsDetail;
    }

}
