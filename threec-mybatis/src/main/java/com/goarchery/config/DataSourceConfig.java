package com.goarchery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "dataSourceServices")
    @ConfigurationProperties(prefix = "spring.datasource.services")
    public DataSource dataSourceOne() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceGoarchery")
    @ConfigurationProperties(prefix = "spring.datasource.goarchery")
    public DataSource dataSourceTwo() {
        return DataSourceBuilder.create().build();
    }
}
