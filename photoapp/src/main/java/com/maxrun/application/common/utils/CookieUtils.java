package com.maxrun.application.common.utils;

//import com.ebs.gslp.common.constants.Constants;
import org.springframework.util.SerializationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {
	
    public static Optional<Cookie> getCookie(String name) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static String getCookieValue(String name) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        name = name.replaceAll("\r", "").replaceAll("\n", "");	// 22.07.06 취약점 조치
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void addCookie(String name, String value, int maxAge) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        value = Optional.ofNullable(value).orElse("");
        Cookie cookie = new Cookie(name, value.replaceAll("\r", "").replaceAll("\n", ""));
        // cookie.setDomain(PropertyManager.get("Globals.domain.url")); // IE Issue
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //cookie.setSecure(Constants.HOME_URL.startsWith("https")); // IE Issue : true인 경우 https 에서만 동작함
        cookie.setSecure(isSecureCookie());
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /** httpOnly=false */
    public static void addPublicCookie(String name, String value, int maxAge) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        value = Optional.ofNullable(value).orElse("");
        Cookie cookie = new Cookie(name, value.replaceAll("\r", "").replaceAll("\n", ""));
        // cookie.setDomain(PropertyManager.get("Globals.domain.url")); // IE Issue
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        //cookie.setSecure(Constants.HOME_URL.startsWith("https")); // IE Issue : true인 경우 https 에서만 동작함
        cookie.setSecure(isSecureCookie());
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(String name) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
    
    private static boolean isSecureCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		/*
		 * String serverName = request.getServerName(); InetAddress address = null; try
		 * { address = InetAddress.getLocalHost(); } catch (UnknownHostException e) {
		 * e.printStackTrace(); } return
		 * !address.getHostAddress().contains("192.168.13") && // 폐쇄망 도메인(운영)
		 * !serverName.contains("localhost");
		 */		 // 로컬
        
        //SSL 채널이고, WAS IP 가 192.168.13 대역이면 secureCookie
        try {
        	if(request.isSecure() && InetAddress.getLocalHost().getHostAddress().contains("192.168.1")) {
            	return true;
            }else {
            	return false;
            }
        }catch(UnknownHostException e) {
        	e.printStackTrace();
        	return false;
        }
        
    }
}
