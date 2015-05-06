package net.afnf.blog.config;

import net.afnf.blog.common.IfModifiedSinceFilter;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public FilterRegistrationBean filter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new IfModifiedSinceFilter());
        bean.setOrder(1);
        return bean;
    }
}