package org.example.service.impl;

import org.example.domain.SeckillGoods;
import org.example.mapper.SeckillGoodsMapper;
import org.example.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:秒杀商品相关操作
 * @author: Frankin
 * @create: 2022-02-14 15:40
 */

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Override
    public SeckillGoods getByGoodsId(Long goodsId) {
        return seckillGoodsMapper.getByGoodsId(goodsId);
    }

    @Override
    public int updateById(SeckillGoods seckillGoods) {
        return seckillGoodsMapper.updateById(seckillGoods);
    }
}
