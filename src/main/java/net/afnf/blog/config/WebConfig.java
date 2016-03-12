package net.afnf.blog.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import net.afnf.blog.common.AssetsFunction;
import net.afnf.blog.common.CachingResourceUrlEncodingFilter;
import net.afnf.blog.common.IfModifiedSinceFilter;
import net.afnf.blog.common.MyApplicationListener;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Log logger = LogFactory.getLog(WebConfig.class);

    @Bean
    @Order(value = 1)
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {

        // システムプロパティーが設定されていればデフォルト実装を使う
        String prop = System.getProperty("ResourceUrlEncodingFilter");
        if (StringUtils.equals(prop, "original")) {
            logger.info("using ResourceUrlEncodingFilter");
            return new ResourceUrlEncodingFilter();
        }
        // そうでなければCachingResourceUrlEncodingFilterを使う
        else {
            logger.info("using CachingResourceUrlEncodingFilter");
            return new CachingResourceUrlEncodingFilter("/static/");
        }
    }

    @Bean
    @Order(value = 2)
    public IfModifiedSinceFilter ifModifiedSinceFilter() {
        return new IfModifiedSinceFilter();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    /** キャッシュ時間 = 1年 */
    public static final int CACHE_PERIOD = 1 * 366 * 24 * 60 * 60;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // buildDateをMANIFEST.MFから取得
        MyApplicationListener.updateBuildDate();

        // 開発時で、さらにJUnitで無い場合は、常にキャッシュせず、Cache-bustingもしない
        boolean devResolver = AppConfig.getInstance().isDevelopment() && AppConfig.getInstance().isTestSite() == false;
        if (devResolver) {
            registry.addResourceHandler("/static/**").addResourceLocations("dummy").setCachePeriod(0).resourceChain(false)
                    .addResolver(new MyPathResourceResolver());
        }
        // それ以外は、PathResourceResolverを使う
        else {
            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/public/static/")
                    .setCachePeriod(CACHE_PERIOD).resourceChain(true).addResolver(new MyVersionResourceResolver());
        }
    }

    /**
     * Cache-bustingをせず、また必要があればminifyするResourceResolver
     */
    private static class MyPathResourceResolver extends PathResourceResolver {

        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {

            // classpath:/public/static/ではなくて、src/main/resourcesを使う
            File f = new File("src/main/resources/public/static/", resourcePath);
            if (f.exists() && f.isFile()) {
                FileSystemResource resource = new FileSystemResource(f);
                return resource;
            }
            else {
                return null;
            }
        }

        @Override
        public String resolveUrlPath(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            // resolveUrlPathする前に、必要があればminifyする
            AssetsFunction.tryUpdate(resourceUrlPath);
            return super.resolveUrlPath(resourceUrlPath, locations, chain);
        }
    }

    /**
     * Cache-bustingのために固定バージョンのURLを生成するVersionResourceResolver
     */
    private static class MyVersionResourceResolver extends VersionResourceResolver {
        public MyVersionResourceResolver() {
            addFixedVersionStrategy(AppConfig.getInstance().getBuildDateYmdhm(), "/**");
        }
    }
}