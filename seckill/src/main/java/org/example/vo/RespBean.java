package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    public static RespBean success(){   //成功只有200这种
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),null);
    }

    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),obj);
    }

    public static RespBean error(RespBeanEnum respBeanEnum){       //失败有很多原因
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),null);

    }

    public static RespBean error(RespBeanEnum respBeanEnum,Object obj){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),obj);
    }
}
