package com.janita.mycat.one.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源bean
 * 连接池
 */
@Configuration
public class DruidDataSourceConfiguration {
	//spring-boot默认的日志是logback
	private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfiguration.class);

	@Value("${spring.datasource.url}")
    private String dbUrl;  
      
    @Value("${spring.datasource.username}")
    private String username;  
      
    @Value("${spring.datasource.password}")
    private String password;  
      
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;  

    @Bean     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource(){  
        DruidDataSource datasource = new DruidDataSource();  
          
        datasource.setUrl(dbUrl);  
        datasource.setUsername(username);  
        datasource.setPassword(password);  
        datasource.setDriverClassName(driverClassName);
        logger.info(datasource.toString());
        return datasource;  
    }  
}
