package com.finalysis.research;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }
}