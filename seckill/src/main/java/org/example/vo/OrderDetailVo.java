package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.OrderInfo;
import org.example.domain.SeckillGoods;

/**
 * @description: 订单详情返回对象
 * @author: Frankin
 * @create: 2022-02-16 15:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo orderInfo;
}
