package org.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 消息生产者
 * @author: Frankin
 * @create: 2022-02-17 14:06
 */
@Slf4j
@Service
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //    public void send(Object msg) {
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("queue", msg);
//    }
//
//    public void send01(Object msg) {
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
//    }
//
//    public void sendRed(Object msg) {
//        log.info("发送red消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
//    }
//
//    public void sendGreen(Object msg) {
//        log.info("发送green消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
//    }

    /**
    * @Description: 发送秒杀信息
    * @Param:
    * @return:
    */
    public void sendSeckillMessage(String msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("seckillExchage", "seckill.message", msg);
    }

}
