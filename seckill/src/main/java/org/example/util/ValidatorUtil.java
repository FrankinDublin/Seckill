package org.example.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ValidatorUtil {

    //创建判断手机号合法表达式
    private static final Pattern mobile_pattern = Pattern.compile("1[3-9]\\d{9}");

    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }
}
