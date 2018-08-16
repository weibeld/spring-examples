package net.weibeld.spring.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Tutorial from http://kaviddiss.com/2015/07/18/building-modern-web-applications-using-java-spring/

@SpringBootApplication
public class UrlApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlApplication.class, args);
    }
}
