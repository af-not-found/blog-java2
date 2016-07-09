package net.afnf.blog;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.PortInUseException;
import org.springframework.context.ConfigurableApplicationContext;

import net.afnf.blog.config.AppConfig;

@SpringBootApplication
@MapperScan("net.afnf.blog.mapper")
public class BlogJava2App {

    private static Log logger = LogFactory.getLog(BlogJava2App.class);

    public static void main(String[] args) throws Exception {
        shutdownAndRun(BlogJava2App.class, args);
    }

    protected static ConfigurableApplicationContext shutdownAndRun(Class<?> clazz, String[] args) {
        try {
            return run(clazz, args);
        }
        // 二重起動
        catch (PortInUseException e) {
            HttpURLConnection connection = null;
            try {
                AppConfig appConfig = AppConfig.getInstance();
                String url = "http://localhost:" + appConfig.getManagementPort() + "/shutdown";
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    logger.info("Shutdown OK");

                    // launch again
                    return run(clazz, args);
                }
                else {
                    throw new IllegalStateException("Invalid response code : " + responseCode);
                }
            }
            catch (Exception e2) {
                throw new IllegalStateException("Shutdown failed", e2);
            }
            finally {
                IOUtils.close(connection);
            }
        }
    }

    private static ConfigurableApplicationContext run(Class<?> clazz, String[] args) {
        SpringApplication app = new SpringApplication(clazz);
        return app.run(args);
    }
}
