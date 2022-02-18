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

    @Update("update sk_goods_seckill set stock_count = stock_count-1 where id = #{id}")
    int updateById(SeckillGoods id);

    //stock_count > 0 和 版本号实现乐观锁 防止超卖
    @Update("update sk_goods_seckill set stock_count = stock_count-1, version= version + 1 where goods_id = #{goodsId} and stock_count > 0 and version = #{version}")
    public int reduceStockByVersion(SeckillGoods seckillGoods);

    // 获取最新版本号
    @Select("select version from sk_goods_seckill  where goods_id = #{goodsId}")
    public int getVersionByGoodsId(@Param("goodsId") long goodsId);
}
