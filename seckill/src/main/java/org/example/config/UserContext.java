package org.example.config;

import org.example.domain.User;

/**
 * @description: 获取登录用户信息
 * @author: Frankin
 * @create: 2022-02-19 15:07
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
