package org.example.service;

import org.example.domain.SeckillOrder;
import org.example.domain.User;

public interface SeckillOrderService {
    SeckillOrder getOrderByUserIdGoodsId(Long id, Long goodsId);

    void save(SeckillOrder order);

    Long getResult(User user, Long goodsId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);
}
