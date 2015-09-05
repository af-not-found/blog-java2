package net.afnf.blog.common;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import net.afnf.blog.config.AppConfig;

@Configuration
public class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(MyApplicationListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        // actuatorの起動は無視
        if (StringUtils.endsWith(contextRefreshedEvent.getApplicationContext().getId(), ":management")) {
            return;
        }

        // ActiveProfileが未指定の場合はエラーとする
        if (StringUtils.isEmpty(AppConfig.getInstance().getActiveProfile())) {
            throw new IllegalStateException("profile is not specified");
        }

        // コンテキストパス正規化
        AppConfig.getInstance().normalizeContextPath();
    }

    public static void updateBuildDate() {
        String buildDate = null;
        try (InputStream inputStream = AppConfig.getInstance().getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (inputStream != null) {
                Manifest manifest = new Manifest(inputStream);
                if (manifest != null) {
                    Attributes attributes = manifest.getMainAttributes();
                    if (attributes != null) {
                        buildDate = attributes.getValue("Build-Date");
                    }
                }
            }
        }
        catch (Throwable e) {
            logger.warn("failed to read MANIFEST.MF, " + e.toString());
        }

        Date target = null;
        if (buildDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(AppConfig.JST);
            target = new Date();
            buildDate = sdf.format(target) + " runtime";
        }
        else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                target = sdf.parse(buildDate);
                sdf.setTimeZone(AppConfig.JST);
                buildDate = sdf.format(target);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Build-Date : " + buildDate);
        AppConfig.getInstance().setBuildDate(buildDate);

        SimpleDateFormat sdfshort = new SimpleDateFormat("yyMMddHHmm");
        sdfshort.setTimeZone(AppConfig.JST);
        AppConfig.getInstance().setBuildDateYmdhm(sdfshort.format(target));
    }
}
