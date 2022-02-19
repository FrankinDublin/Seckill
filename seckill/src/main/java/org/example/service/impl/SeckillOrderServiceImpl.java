package org.example.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.mapper.SeckillOrderMapper;
import org.example.service.SeckillOrderService;
import org.example.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    /**
    * @Description: 获取秒杀地址
    * @Param:
    * @return:
    */
    @Override
    public String createPath(User user, Long goodsId) {
        //生成随机验证码存入redis进行验证
        String str = MD5Util.md5(UUID.randomUUID()+"123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
    * @Description: 检验秒杀地址正确性
    * @Param:
    * @return:
    */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user==null || goodsId<0 || StringUtils.isEmpty(path)) return false;
        String key = "seckillPath:"+user.getId()+":"+goodsId;
        String redisPath = (String) redisTemplate.opsForValue().get(key);
        return path.equals(redisPath);
    }
}
