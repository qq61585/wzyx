package com.wzyx.controller.common;

import com.wzyx.common.enumration.RedisExpireTime;
import com.wzyx.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 拦截移动端的请求，当请求中携带有authToken字段的时候，
 * 更新Redis缓存中用户的有效时间
 */
@WebFilter(filterName = "AuthTokenFilter")
public class AuthTokenFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String authToken = request.getParameter("authToken");
        if (!StringUtils.isBlank(authToken)) {
//            request中含有authToken字段，那么就更新Redis中的缓存时间
            String userString = RedisPoolUtil.get(authToken);
            if (StringUtils.isNoneBlank(userString)) {
//                authToken在Redis缓存中有对应的字段，更新该authToken对应的时间
                RedisPoolUtil.expire(authToken, RedisExpireTime.USER_EXPIRE_TIME.getTime());
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
