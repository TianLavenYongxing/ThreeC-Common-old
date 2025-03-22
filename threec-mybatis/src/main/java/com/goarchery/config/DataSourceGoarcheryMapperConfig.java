package com.goarchery.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.goarchery.dao.goarchery", sqlSessionFactoryRef = "sqlSessionFactoryGoarchery")
public class DataSourceGoarcheryMapperConfig {
}
