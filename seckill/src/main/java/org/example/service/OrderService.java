package org.example.service;

import org.example.domain.OrderInfo;
import org.example.domain.User;
import org.example.vo.GoodsVo;

public interface OrderService {
    OrderInfo seckill(User user, GoodsVo goodsVo);
}
