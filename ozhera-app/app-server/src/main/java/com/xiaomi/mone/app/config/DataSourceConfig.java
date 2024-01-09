package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import run.mone.rdbms.DatasourceConfig;
import run.mone.rdbms.DatasourceUtil;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 11:34
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.default.initialPoolSize}")
    private Integer defaultInitialPoolSize;

    @Value("${spring.datasource.default.maxPoolSize}")
    private Integer defaultMaxPoolSize;

    @Value("${spring.datasource.default.minialPoolSize}")
    private Integer defaultMinPoolSize;

    @NacosValue(value = "${spring.datasource.username}", autoRefreshed = true)
    private String dataSourceUserName;

    @NacosValue(value = "${spring.datasource.url}", autoRefreshed = true)
    private String dataSourceUrl;

    @NacosValue(value = "${spring.datasource.password}", autoRefreshed = true)
    private String dataSourcePasswd;

    @Value("${server.type}")
    private String serverType;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws PropertyVetoException, NamingException {
        log.info("DataSourceConfig {} {} {}", serverType, dataSourceUrl, dataSourceUserName);

        DatasourceConfig datasourceConfig = DatasourceConfig.builder()
                .dataSourceUrl(dataSourceUrl)
                .driverClass(driverClass)
                .dataSourceUserName(dataSourceUserName)
                .dataSourcePasswd(dataSourcePasswd)
                .minPoolSize(defaultMinPoolSize)
                .maxPoolSize(defaultMaxPoolSize)
                .initialPoolSize(defaultInitialPoolSize)
                .build();

        DataSource dataSource = DatasourceUtil.newDataSource(datasourceConfig);
        log.warn("dataSourceUrl:{} init success", dataSourceUrl);
        return dataSource;
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() throws PropertyVetoException, NamingException {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("masterDataSource") DataSource masterDataSource) {
        JdbcTemplate jt = new JdbcTemplate(masterDataSource);
        return jt;
    }


    @Bean
    public SpringDaoRunner springDaoRunner() {
        return new SpringDaoRunner();
    }

    @Bean
    public Dao dao(@Qualifier("masterDataSource") DataSource masterDataSource, SpringDaoRunner springDaoRunner) {
        NutDao dao = new NutDao(masterDataSource);
        dao.setRunner(springDaoRunner);
        return dao;
    }
}
