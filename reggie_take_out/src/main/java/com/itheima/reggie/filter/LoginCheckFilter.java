package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器:检查用户是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}", request.getRequestURI());
        //不需要处理的请求,登录和推出，静态资源.
        String[] urls = new String[]{"/employee/login", "/employee/logout", "/backend/**", "/front/**"};
        //判断请求是否处理
        boolean check = check(urls, requestURI);
        if (check) {
            filterChain.doFilter(request, response);
            //放行，结束方法。
            return;
        }
        //判断登录状态,如果登录直接放行
        if (request.getSession().getAttribute("employee") != null) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //如果未登录返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            //true匹配上
            boolean math = PATH_MATCHER.match(url, requestURI);
            if (math) {
                return true;
            }
        }
        return false;
    }
}
