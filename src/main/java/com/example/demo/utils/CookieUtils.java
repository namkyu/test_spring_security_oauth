package com.example.demo.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static Cookie generateCookie(String name, String value, String path, boolean httpOnly, int maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        if (domain != null) {
            cookie.setDomain(domain);
        }

        return cookie;
    }

    public static Cookie generateJwtHttpOnlyCookie(String name, String value, int maxAge) {
        Cookie cookie = generateCookie(name, value, "/", true, maxAge, null);
        return cookie;
    }

    public static String getCookie(HttpServletRequest request, String key) {
        String sessionCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    sessionCookie = cookie.getValue();
                    break;
                }
            }
        }

        return sessionCookie;
    }
}
