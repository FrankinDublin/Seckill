package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.domain.SeckillOrder;

@Mapper
public interface SeckillOrderMapper {
    @Select("select id from sk_order where user_id = #{userId} and goods_id = #{goodsId}")
    SeckillOrder getOrderByUserIdGoodsId(@Param("userId") long id, @Param("goodsId") long goodsId);

    @Insert("insert into sk_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    int insert(SeckillOrder order);
}
