package org.example.controller;

import org.example.domain.OrderInfo;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SeckillOrderService;
import org.example.vo.GoodsVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 优化前：790
 * 加缓存：1453
 * @description: 处理秒杀业务
 * @author: Frankin
 * @create: 2022-02-14 14:28
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    OrderService orderService;

    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user,Long goodsId){
        /**
        * @Description: 秒杀业务处理
        * @Param: [model, user, goodsId]
        * @return: 跳转网址
        */
        if(user == null) return "login";
        model.addAttribute("user",user);
        //根据商品id获取商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        if(goodsVo.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断用户有没有买过
        SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEAT_SECKILL.getMessage());
            return "seckillFail";
        }
        //下单
        OrderInfo orderInfo = orderService.seckill(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }



}
