package org.example.controller;

import com.alibaba.fastjson.JSON;
import org.example.domain.OrderInfo;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.rabbitmq.MQSender;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SeckillOrderService;
import org.example.vo.GoodsVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.example.vo.SeckillMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优化前：790
 * 加缓存：1453
 * @description: 处理秒杀业务
 * @author: Frankin
 * @create: 2022-02-14 14:28
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MQSender mqSender;
    Map<Long,Boolean> emptyStockMap = new HashMap<>();  //标记商品是否为空，减少售空后访问redis的次数

    @PostMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(User user,Long goodsId){
        /**
        * @Description: 秒杀业务处理
        * @Param: [model, user, goodsId]
        * @return: 跳转网址
        */
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断用户有没有买过
        SeckillOrder order = ((SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId));
        //SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        //内存标记商品是否售空，减少redis访问
        if(emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        //预减库存
        Long stock = valueOperations.increment("seckillGoods:" + goodsId, -1);
        if(stock<0){
            emptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId, 1); //库存会减到-1，这里补上
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        //信息交给队列，在队列处下单，异步处理
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        //返回正在排队中的消息
        return RespBean.success(0);

        /*  //开始通过redis预减库存优化，舍弃原有逻辑
        //根据商品id获取商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        if(goodsVo.getStockCount()<1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断用户有没有买过
        SeckillOrder order = ((SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        //SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        //下单
        OrderInfo orderInfo = orderService.seckill(user,goodsVo);
        if(orderInfo==null) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        return RespBean.success(orderInfo);
        */

    }
    /**
    * @Description: 获取秒杀结果
    * @Param: 
    * @return: orderId：秒杀成功；-1：秒杀失败；0：排队中
    */ 
    @RequestMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
    
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user,Long goodsId){
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


    /**
    * @Description: 系统初始化时，将库存加载进入redis
    * @Param:
    * @return: 
    */ 
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVo->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
