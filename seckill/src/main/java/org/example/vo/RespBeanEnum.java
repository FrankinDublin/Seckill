package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //登录模块5002xx
    LOGIN_ERROR(500210,"密码错误"),
    MOBILE_ERROR(500211,"该手机号未注册"),
    BIND_ERROR(500212,"手机号校验出错"),
    SESSION_ERROR(500213,"用户不存在"),
    //订单模块5003XX
    ORDER_NOT_EXIST(500310,"订单信息不存在"),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEAT_SECKILL(500501,"不能重复秒杀"),
    ;


    private final Integer code;
    private final String message;
}
