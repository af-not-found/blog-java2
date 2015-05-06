package net.afnf.blog;

import java.net.HttpURLConnection;
import java.net.URL;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BlogJava2App {

    private static Log logger = LogFactory.getLog(BlogJava2App.class);

    public static void main(String[] args) throws Exception {
        shutdownAndRun(BlogJava2App.class, args);
    }

    protected static ConfigurableApplicationContext shutdownAndRun(Class<?> clazz, String[] args) {

        try {
            return SpringApplication.run(clazz, args);
        }
        catch (Exception e) {

            // java.net.BindException: Address already in use: bind
            if ("Tomcat connector in failed state".equals(e.getMessage())) {

                HttpURLConnection connection = null;
                try {
                    String url = "http://localhost:" + AppConfig.getInstance().getManagementPort() + "/shutdown";
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("POST");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        logger.info("Shutdown OK");

                        // launch again
                        return SpringApplication.run(clazz, args);
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
            else {
                throw e;
            }
        }
    }
}
