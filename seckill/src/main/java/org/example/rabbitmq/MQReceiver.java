package org.example.rabbitmq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.vo.GoodsVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.example.vo.SeckillMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @description: 消息消费者
 * @author: Frankin
 * @create: 2022-02-17 14:09
 */
@Slf4j
@Service
public class MQReceiver {
    //    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("fanout01接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("fanout02接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("direct01接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("direct02接收消息："+msg);
//    }
    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    OrderService orderService;

    /**
     * @Description:下单操作
     * @Param:
     * @return:
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("接收消息：" + msg);
        SeckillMessage seckillMessage = JSON.parseObject(msg, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        //判断用户有没有买过
        SeckillOrder order = ((SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        if(order!=null){
            return;
        }
        //下单操作
        orderService.seckill(user,goodsVo);
    }
}
