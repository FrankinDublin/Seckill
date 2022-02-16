package org.example.service;

import org.example.vo.GoodsVo;

import java.util.List;

public interface GoodsService {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);

}
