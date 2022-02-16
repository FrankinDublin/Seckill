package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.domain.SeckillGoods;
import org.example.vo.GoodsVo;

@Mapper
public interface SeckillGoodsMapper {

    @Select("select * from sk_goods_seckill where goods_id=#{goodsId}")
    SeckillGoods getByGoodsId(@Param("goodsId") Long goodsId);

    @Update("update sk_goods_seckill set stock_count = #{stockCount} where id = #{id} and stock_count>0")
    int updateById(SeckillGoods id);
}
