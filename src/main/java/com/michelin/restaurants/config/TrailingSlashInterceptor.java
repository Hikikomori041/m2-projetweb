package com.michelin.restaurants.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TrailingSlashInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.length() > 1 && uri.endsWith("/")) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", uri.substring(0, uri.length() - 1));
            return false;
        }
        return true;
    }
}

/* Note

Les fichiers TrailingSlashInterceptor et WebConfig ne sont là que pour rogner les routes qui se terminent par "/".
*/