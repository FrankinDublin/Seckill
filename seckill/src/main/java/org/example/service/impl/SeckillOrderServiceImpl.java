package org.example.service.impl;

import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.mapper.SeckillOrderMapper;
import org.example.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Frankin
 * @create: 2022-02-14 14:58
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    SeckillOrderMapper seckillOrderMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public SeckillOrder getOrderByUserIdGoodsId(Long id, Long goodsId) {
        SeckillOrder orderByUserIdGoodsId = seckillOrderMapper.getOrderByUserIdGoodsId(id, goodsId);
        return orderByUserIdGoodsId;
    }

    @Override
    public void save(SeckillOrder order) {
        seckillOrderMapper.insert(order);
    }

    /**
     * @Description: 获取秒杀结果
     * @Param:
     * @return: orderId：秒杀成功；-1：秒杀失败；0：排队中
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) return seckillOrder.getOrderId();
        else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
            //该商品已售空，秒杀失败
            return -1L;
        } else return 0L;

    }
}
