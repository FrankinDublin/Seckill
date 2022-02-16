package org.example.controller;

import org.example.domain.User;
import org.example.service.OrderService;
import org.example.vo.OrderDetailVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 订单数据处理
 * @author: Frankin
 * @create: 2022-02-16 15:09
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @RequestMapping("/detail")
    public RespBean detail(User user, Long orderId){
        if(user==null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }


}
