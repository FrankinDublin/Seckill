package org.example.service.impl;

import org.example.mapper.GoodsMapper;
import org.example.service.GoodsService;
import org.example.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: Frankin
 * @create: 2022-02-12 14:14
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;
    @Override
    public List<GoodsVo> findGoodsVo() {
        /**
        * @Description: 获取商品列表
        * @Param: []
        * @return: java.util.List<org.example.vo.GoodsVo>
        */
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }
}
