package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.domain.User;

/**
 * @description: 商品详情返回对象
 * @author: Frankin
 * @create: 2022-02-16 14:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int seckillStatus = 0;
    private int remainSeconds = 0;
}
