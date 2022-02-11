package org.example.vo;

import lombok.Data;
import lombok.ToString;
import org.example.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class LoginVo {
    //接收前端的手机号和密码信息
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)   //固定最小长度
    private String password;
}
