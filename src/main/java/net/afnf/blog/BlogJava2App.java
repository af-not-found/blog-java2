package net.afnf.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogJava2App {

    public static void main(String[] args) throws Exception {
        new SpringApplication(BlogJava2App.class).run(args);
    }
}
