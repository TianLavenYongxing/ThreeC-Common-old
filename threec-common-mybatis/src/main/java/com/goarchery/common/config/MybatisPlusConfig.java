package com.goarchery.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.goarchery.common.config.handler.FieldMetaObjectHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {


    @Primary
    @Bean(name = "sqlSessionFactoryServices")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryServices(@Qualifier("dataSourceServices") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/services/*.xml"));
        factoryBean.setGlobalConfig(createGlobalConfig());
        factoryBean.setPlugins(mybatisPlusInterceptor());
        return factoryBean;
    }

    @Bean(name = "sqlSessionFactoryGoarchery")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryGoarchery(@Qualifier("dataSourceGoarchery") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/goarchery/*.xml"));
        factoryBean.setGlobalConfig(createGlobalConfig());
        factoryBean.setPlugins(mybatisPlusInterceptor());
        return factoryBean;
    }

    @Primary
    @Bean(name = "transactionManagerServices")
    public PlatformTransactionManager transactionManagerServices(
            @Qualifier("dataSourceServices") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "transactionManagerGoarchery")
    public PlatformTransactionManager transactionManagerGoarchery(
            @Qualifier("dataSourceGoarchery") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 分页插件
        return interceptor;
    }

    private GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setMetaObjectHandler(new FieldMetaObjectHandler());
        return globalConfig;
    }
}

