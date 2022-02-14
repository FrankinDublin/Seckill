package org.example.service.impl;

import org.example.domain.SeckillOrder;
import org.example.mapper.SeckillOrderMapper;
import org.example.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Override
    public SeckillOrder getOrderByUserIdGoodsId(Long id, Long goodsId) {
        SeckillOrder orderByUserIdGoodsId = seckillOrderMapper.getOrderByUserIdGoodsId(id, goodsId);
        return orderByUserIdGoodsId;
    }

    @Override
    public void save(SeckillOrder order) {
        seckillOrderMapper.insert(order);
    }
}
