package org.example.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 操作cookie
 * @author: Frankin
 * @create: 2022-02-11 13:31
 */
public class CookieUtil {
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {

        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 找到指定的cookie
                if (cookie != null && cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void addCookie(HttpServletResponse response, String key, String value){
        Cookie cookie = new Cookie(key,value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
