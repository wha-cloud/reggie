package com.haw.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.haw.reggie.common.BaseContext;
import com.haw.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户是否已经登录
* */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")  //名称无所谓，urlpatterns表示要拦截哪些路径,/*表示所有
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        1、获取本次请求的URI
        String requestURL = request.getRequestURI();
        log.info("拦截到请求：{}",request.getRequestURI());
        //定义不需要拦截的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login"  //移动端登录
        };

//        2、判断本次请求是否需要处理
        boolean check = check(urls, requestURL);

//        3、如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURL);
            filterChain.doFilter(request,response);
            return;
        }

//        4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户{}已经登录",request.getSession().getAttribute("employee"));
            Long emId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(emId);
            filterChain.doFilter(request,response);
            return;
        }

        //        4-2、判断移动端登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户{}已经登录",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

//        5、如果未登录则返回未登录结果，通过输出流向客户端页面输出响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


//        log.info("拦截到请求：{}",request.getRequestURI());
//        filterChain.doFilter(request,response);
    }

    /**
     * 路径匹配，检查是都要拦截
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls,String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURL);
            if(match)
                return true;
        }
        return false;
    }
}
