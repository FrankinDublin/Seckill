package org.example.service;

import org.example.domain.SeckillGoods;

public interface SeckillGoodsService {
    SeckillGoods getByGoodsId(Long goodsId);

    int updateById(SeckillGoods seckillGoods);

    boolean reduceStock(SeckillGoods seckillGoods);
}
