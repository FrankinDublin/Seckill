package org.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @description: 消息消费者
 * @author: Frankin
 * @create: 2022-02-17 14:09
 */
@Slf4j
@Service
public class MQReceiver {
    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接收消息："+msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("fanout01接收消息："+msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("fanout02接收消息："+msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg){
        log.info("direct01接收消息："+msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object msg){
        log.info("direct02接收消息："+msg);
    }
}
