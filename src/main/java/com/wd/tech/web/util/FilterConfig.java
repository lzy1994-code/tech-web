package com.wd.tech.web.util;

import com.wd.tech.web.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xyj on 2018/9/4.
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LoginFilter());
        registration.addUrlPatterns("/user/verify/*","/chat/verify/*","/group/verify/*","/information/verify/*","/community/verify/*","/tool/verify/*");
        registration.setName("loginFilter");
        registration.setOrder(1);
        return registration;
    }

}
