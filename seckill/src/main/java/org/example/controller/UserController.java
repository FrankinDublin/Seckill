package org.example.controller;

import org.example.domain.User;
import org.example.rabbitmq.MQSender;
import org.example.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 压力测试
 * @author: Frankin
 * @create: 2022-02-15 15:30
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        /**
         * @Description: 测试简单模式
         * @Param: []
         * @return: void
         */
        mqSender.send("hello");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01() {
        /**
         * @Description: 测试fanout模式
         * @Param: []
         * @return: void
         */
        mqSender.send01("hello");
    }

    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02() {
        /**
         * @Description: 测试direct模式
         * @Param: []
         * @return: void
         */
        mqSender.sendRed("hello");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03() {
        /**
         * @Description: 测试direct模式
         * @Param: []
         * @return: void
         */
        mqSender.sendGreen("hello");
    }
}
