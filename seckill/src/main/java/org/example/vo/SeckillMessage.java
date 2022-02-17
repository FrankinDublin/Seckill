package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.User;

/**
 * @description: 秒杀信息对象
 * @author: Frankin
 * @create: 2022-02-17 15:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}
