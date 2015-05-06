package net.afnf.blog.config;

import net.afnf.blog.thymeleaf.AppConfigDialect;
import net.afnf.blog.thymeleaf.AssetsDialect;
import net.afnf.blog.thymeleaf.MyFunctionDialect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

@Configuration
public class ThymeleafConfig {

    @Autowired
    public TemplateEngine templateEngine;

    @Bean
    public TemplateEngine addDialect() {
        templateEngine.addDialect(new AssetsDialect());
        templateEngine.addDialect(new MyFunctionDialect());
        templateEngine.addDialect(new AppConfigDialect());
        return templateEngine;
    }
}
