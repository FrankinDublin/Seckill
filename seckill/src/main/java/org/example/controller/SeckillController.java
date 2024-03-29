package org.example.controller;

import com.alibaba.fastjson.JSON;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.OrderInfo;
import org.example.domain.SeckillOrder;
import org.example.domain.User;
import org.example.exception.GlobalException;
import org.example.limiter.AccessLimit;
import org.example.rabbitmq.MQSender;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SeckillOrderService;
import org.example.vo.GoodsVo;
import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.example.vo.SeckillMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 优化前：790
 * 加缓存：1453
 * 优化后：1818
 * @description: 处理秒杀业务
 * @author: Frankin
 * @create: 2022-02-14 14:28
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MQSender mqSender;
    Map<Long,Boolean> emptyStockMap = new HashMap<>();  //标记商品是否为空，减少售空后访问redis的次数

    @PostMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable("path")String path, User user,Long goodsId){
        /**
        * @Description: 秒杀业务处理
        * @Param: [model, user, goodsId]
        * @return: 跳转网址
        */
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean res = seckillOrderService.checkPath(user,goodsId,path);
        if(!res) return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        //判断用户有没有买过
        SeckillOrder order = ((SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId));
        //SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        //内存标记商品是否售空，减少redis访问
        if(emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        //预减库存
        Long stock = valueOperations.increment("seckillGoods:" + goodsId, -1);

        if(stock<0){
            try {
                afterPropertiesSet();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //由于更新数据库的操作有可能取消，导致预减库存却没有实际购买，需要确认真实库存
            Long stock2 = valueOperations.increment("seckillGoods:" + goodsId, -1);
            if(stock2<0){
                emptyStockMap.put(goodsId,true);
                valueOperations.increment("seckillGoods:" + goodsId, 1); //库存会减到-1，这里补上
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
        }
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        //信息交给队列，在队列处下单，异步处理
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        //返回正在排队中的消息
        return RespBean.success(0);

        /*  //开始通过redis预减库存优化，舍弃原有逻辑
        //根据商品id获取商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        if(goodsVo.getStockCount()<1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断用户有没有买过
        SeckillOrder order = ((SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        //SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        //下单
        OrderInfo orderInfo = orderService.seckill(user,goodsVo);
        if(orderInfo==null) return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        return RespBean.success(orderInfo);
        */

    }

    @RequestMapping("/path")
    @ResponseBody
    @AccessLimit(second = 5,maxCount = 5)
    public RespBean getPath(User user,Long goodsId,String captcha,HttpServletRequest request){
        if(user==null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String uri = request.getRequestURI();
        captcha = "0";
        //设置限流，5秒内允许5次访问，注解实现
        /*Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if(count==null){
            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count>5){
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }else {
            valueOperations.increment(uri+":"+user.getId(),1);
        }*/
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check)
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        String str = seckillOrderService.createPath(user,goodsId);
        return RespBean.success(str);
    }
    /**
    * @Description: 获取秒杀结果
    * @Param: 
    * @return: orderId：秒杀成功；-1：秒杀失败；0：排队中
    */ 
    @RequestMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    @RequestMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if(user==null || goodsId<0)
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        response.setContentType("image/jpg");
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败"+e.getMessage());
        }
    }
    
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user,Long goodsId){
        /**
         * @Description: 秒杀业务处理
         * @Param: [model, user, goodsId]
         * @return: 跳转网址
         */
        if(user == null) return "login";
        model.addAttribute("user",user);
        //根据商品id获取商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        if(goodsVo.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断用户有没有买过
        SeckillOrder order =seckillOrderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEAT_SECKILL.getMessage());
            return "seckillFail";
        }
        //下单
        OrderInfo orderInfo = orderService.seckill(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }


    /**
    * @Description: 系统初始化时，将库存加载进入redis
    * @Param:
    * @return: 
    */ 
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVo->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
