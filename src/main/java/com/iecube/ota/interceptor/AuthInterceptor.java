package com.iecube.ota.interceptor;

import com.iecube.ota.config.AppAccessTokenConfig;
import com.iecube.ota.utils.FeiShu.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    private AppAccessTokenConfig appAccessTokenConfig;

    public List<String> WHITE_LIST = Arrays.asList(
            "/",
            "/auth/*",
            "/auth/**",
            "/callback/*",
            "/callback/**",
            "/files/file/**",
            "/callback/fs"
            );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        log.info(requestUri);
        for (String pattern : WHITE_LIST) {
            if (new AntPathMatcher().match(pattern, requestUri)) {
                return true; // 允许通过
            }
        }
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            log.warn("请求头错误");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if (AuthUtil.authed(authorization, appAccessTokenConfig.getAppAccessToken(), redisTemplate)) {
            return true;
        }
        log.warn("鉴权未通过");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthUtil.clean();
    }
}
