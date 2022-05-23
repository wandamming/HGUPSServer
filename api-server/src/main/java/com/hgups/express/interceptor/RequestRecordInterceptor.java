package com.hgups.express.interceptor;

import com.hgups.express.config.shiro.MySessionManager;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RequestRecordInterceptor extends HandlerInterceptorAdapter {
    private static final List<String> EXCLUDE_URLS = new ArrayList<>();

    static {
        EXCLUDE_URLS.add("/hgups/user/out");
        EXCLUDE_URLS.add("/hgups/user/login");
        EXCLUDE_URLS.add("/hgups/user/register");
        EXCLUDE_URLS.add("/hgups/swagger-ui.html");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String remoteHost = request.getRemoteHost();
        String requestURI = request.getRequestURI();
        String accessToken = request.getHeader(MySessionManager.AUTHORIZATION);

        log.info("preHandle.remoteHost:{} uri:{} accessToken:{}", remoteHost, requestURI, accessToken);

//        if(!EXCLUDE_URLS.contains(requestURI)
//                && !requestURI.startsWith("/hgups/webjars/")
//                && !requestURI.startsWith("/hgups/swagger-resources")
//                && !requestURI.startsWith("/hgups/v2/api-docs")
//                && !requestURI.startsWith("/hgups/bigdata")) {
//            Long id = ShiroUtil.getLoginUserId();
//            log.info("preHandle userId: {}", id);
//        }

        return super.preHandle(request, response, handler);
    }
}
