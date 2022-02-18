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
    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Override
    public SeckillGoods getByGoodsId(Long goodsId) {
        return seckillGoodsMapper.getByGoodsId(goodsId);
    }

    @Override
    public int updateById(SeckillGoods seckillGoods) {
        return seckillGoodsMapper.updateById(seckillGoods);
    }

    @Override
    public boolean reduceStock(SeckillGoods seckillGoods) {
        int numAttempts = 0;
        int res = 0;
        do {
            numAttempts++;
            //获取最新版本号
            seckillGoods.setVersion(seckillGoodsMapper.getVersionByGoodsId(seckillGoods.getGoodsId()));
            //在最新版本号情况下修改
            res = seckillGoodsMapper.reduceStockByVersion(seckillGoods);
            if (res != 0) break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);
        return res > 0;
    }
}
