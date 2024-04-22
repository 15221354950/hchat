package com.hong.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

import static com.hong.config.DataSourceMybatisConfig.SQL_SESSION_FACTORY;
import static com.hong.config.DataSourceMybatisConfig.SQL_SESSION_TEMPLATE;

/**
 * @description: order 数据源
 * @author: duanlongshan@dushu365.com
 * @time: 2021/6/29 5:38 下午
 */

@MapperScan(basePackages = {"com.hong.**.*"}, sqlSessionFactoryRef = SQL_SESSION_FACTORY, sqlSessionTemplateRef = SQL_SESSION_TEMPLATE)
@Configuration
@PropertySource(value = {"classpath:datasource.properties", "classpath:user.properties"})
public class DataSourceMybatisConfig {

    public static final String SQL_SESSION_FACTORY = "sqlSessionFactory";

    public static final String SQL_SESSION_TEMPLATE = "sqlSessionTemplate";

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.driver-class-name}")
    private String driverClassName;

    @Value("${datasource.hikari.connection-timeout}")
    private Integer connectionTimeout;

    @Value("${datasource.hikari.idle-timeout}")
    private Integer idleTimeout;

    @Value("${datasource.hikari.maximum-pool-size}")
    private Integer maxPoolSize;

    @Value("${datasource.hikari.minimum-idle}")
    private Integer minPoolSize;

    /**
     * 创建数据源
     *
     * @return
     */
    @Bean("dataSource")
    public DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setConnectionInitSql("set names utf8mb4;");
        hikariDataSource.setIdleTimeout(idleTimeout);
        hikariDataSource.setConnectionTimeout(connectionTimeout);
//        hikariDataSource.setMaxLifetime();
        hikariDataSource.setMaximumPoolSize(maxPoolSize);
        hikariDataSource.setMinimumIdle(minPoolSize);
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        return hikariDataSource;
    }


    @Bean(SQL_SESSION_FACTORY)
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCallSettersOnNulls(true);
        configuration.setDefaultStatementTimeout(60); //秒 待验证
        sqlSessionFactoryBean.setConfiguration(configuration);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources("classpath:com/hong/**/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean("transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(SQL_SESSION_TEMPLATE)
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(SQL_SESSION_FACTORY) SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }

}
