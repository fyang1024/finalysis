package com.finalysis.research;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.boot.SpringApplication.run;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class DataRestConfig extends RepositoryRestMvcConfiguration {
}