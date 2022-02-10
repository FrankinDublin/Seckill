package org.example.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";  //默认盐
    
    
    public static String inputPassToFormPass(String inputPass){
        /**
        * @Description: 第一次加密，避免在网络传输被截取然后反推出密码，所以在md5加密前先打乱密码
        * @Param: [inputPass:输入的原密码]
        * @return: java.lang.String
        */
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        /**
        * @Description: 第二次加密，结果存至数据库
        * @Param: [formPass：一次加密结果, salt：用户对应的盐]
        * @return: java.lang.String
        */
        String str = ""+salt.charAt(1)+salt.charAt(2) + formPass +salt.charAt(salt.length()-1) + salt.charAt(salt.length()-2);
        return md5(str);
    }

    public static String inputPassToDbPass(String input, String saltDB){
        /**
        * @Description: 合并两次加密
        * @Param: [input：前端的明文密码, saltDB：用户盐]
        * @return: java.lang.String：存数据库的加密结果
        */
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    //即使前端传输的明文密码被截取，攻击者也无法通过明文密码找出加密后的密码
}
