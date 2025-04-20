package com.goarchery.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.goarchery.dao.services", sqlSessionFactoryRef = "sqlSessionFactoryServices")
public class DataSourceServicesMapperConfig {

}

