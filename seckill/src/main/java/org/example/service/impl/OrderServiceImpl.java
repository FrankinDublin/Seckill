package org.example.service.impl;

import org.example.domain.OrderInfo;
import org.example.domain.SeckillGoods;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.exception.GlobalException;
import org.example.mapper.OrderMapper;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SeckillGoodsService;
import org.example.service.SeckillOrderService;
import org.example.vo.GoodsVo;
import org.example.vo.OrderDetailVo;
import org.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description:秒杀商品相关操作
 * @author: Frankin
 * @create: 2022-02-14 14:58
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Transactional
    @Override
    public OrderInfo seckill(User user, GoodsVo goodsVo) {
        //一个商品对应一个秒杀商品
        SeckillGoods seckillGoods = seckillGoodsService.getByGoodsId(goodsVo.getId());
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        if(seckillGoods.getStockCount()<0) return null;
        //更新秒杀商品库存数量
        int res = seckillGoodsService.updateById(seckillGoods);
        if(res==0) return null;
        OrderInfo orderInfo = new OrderInfo();
        //生成订单
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setCreateDate(new Date());
        orderMapper.insert(orderInfo);
        //生成秒杀订单
        SeckillOrder order = new SeckillOrder();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setOrderId(orderInfo.getId());
        seckillOrderService.save(order);
        //订单结果存入redis
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goodsVo.getId(),order);
        return orderInfo;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        /**
        * @Description: 生成订单详情信息
        * @Param: 订单编号
        * @return: org.example.vo.OrderDetailVo
        */
        if(orderId == null) throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        OrderInfo orderById = orderMapper.getOrderById(orderId);
        GoodsVo byGoodsId = goodsService.findGoodsVoByGoodsId(orderById.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrderInfo(orderById);
        orderDetailVo.setGoodsVo(byGoodsId);
        return orderDetailVo;
    }
}
