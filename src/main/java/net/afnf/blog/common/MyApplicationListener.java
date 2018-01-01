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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;

import net.afnf.blog.config.AppConfig;

@Configuration
@DependsOn("appConfig")
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
    }

    public static void updateBuildDate() {

        // BuildDate未設定の場合
        AppConfig appConfig = AppConfig.getInstance();
        if (StringUtils.isBlank(appConfig.getBuildDate())) {

            // MANIFEST.MFからのbuildDate取得を試みる（productionのみ）
            String buildDateStr = null;
            Date buildDate = null;
            try (InputStream inputStream = appConfig.getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
                if (inputStream != null) {
                    Manifest manifest = new Manifest(inputStream);
                    if (manifest != null) {
                        Attributes attributes = manifest.getMainAttributes();
                        if (attributes != null) {
                            // パース
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            buildDate = sdf.parse(attributes.getValue("Build-Date"));
                            sdf.setTimeZone(AppConfig.JST);
                            buildDateStr = sdf.format(buildDate);
                        }
                    }
                }
            }
            catch (Throwable e) {
                logger.info("failed to read MANIFEST.MF, e=" + e.toString() + ", continue...");
            }

            // 取得できない場合は、現在時刻からbuildDateを生成
            if (buildDateStr == null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                sdf.setTimeZone(AppConfig.JST);
                buildDate = new Date();
                buildDateStr = sdf.format(buildDate) + " runtime";
            }

            // AppConfigに設定
            logger.info("Build-Date : " + buildDateStr);
            appConfig.setBuildDate(buildDateStr);

            // AssetPileline用の日付をAppConfigに設定
            SimpleDateFormat sdfshort = new SimpleDateFormat("yyMMddHHmm");
            sdfshort.setTimeZone(AppConfig.JST);
            appConfig.setBuildDateYmdhm(sdfshort.format(buildDate));
        }
    }
}
