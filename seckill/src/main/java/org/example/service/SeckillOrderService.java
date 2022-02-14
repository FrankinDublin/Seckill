package org.example.service;

import org.example.domain.SeckillOrder;

public interface SeckillOrderService {
    SeckillOrder getOrderByUserIdGoodsId(Long id, Long goodsId);

    void save(SeckillOrder order);
}
