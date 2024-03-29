package org.example.exception;

import org.example.vo.RespBean;
import org.example.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @description: 全局异常处理类
 * @author: Frankin
 * @create: 2022-02-11 13:06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException)e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException ex = (BindException)e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常："+ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        //return RespBean.error(RespBeanEnum.ERROR);
        return new RespBean(1,e.getMessage(),null);
    }
}
