package net.afnf.blog.common;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

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

        // buildDateをMANIFEST.MFから取得
        updateBuildDate();
    }

    protected void updateBuildDate() {
        String buildDate = null;
        try (InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
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

        if (buildDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            buildDate = sdf.format(new Date()) + " runtime";
        }

        logger.info("Build-Date : " + buildDate);
        AppConfig.getInstance().setBuildDate(buildDate);
    }
}
