package com.wd.tech.web.filter;

import com.wd.tech.rpc.api.UserRpcService;
import com.wd.tech.web.util.BwJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by xyj on 2017/10/9.
 */
public class LoginFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Resource
    private UserRpcService userRpcService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
       this.userRpcService = (UserRpcService)ctx.getBean("userRpcService", UserRpcService.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        String userId = request.getHeader("userId");
        String sessionId = request.getHeader("sessionId");
        String ak = request.getHeader("ak");

        logger.info("userId={},sessionId={},ak={}",userId,sessionId,ak);

        if(userId == null || userId.equals("") || sessionId == null || sessionId.equals(""))
        {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println(BwJsonHelper.returnJSON("0001","请先登陆"));
            out.flush();
            return;
        }

        int uid = Integer.valueOf(userId);
       boolean flag = userRpcService.checkUserLoginStatus(uid,sessionId);

        if(flag)
        {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(BwJsonHelper.returnJSON("1001","请先登陆"));
        out.flush();
        return;

    }

    @Override
    public void destroy() {

    }
}
