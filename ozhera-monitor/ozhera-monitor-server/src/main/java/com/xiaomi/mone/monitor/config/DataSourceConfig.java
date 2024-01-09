/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.monitor.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import run.mone.rdbms.DatasourceConfig;
import run.mone.rdbms.DatasourceUtil;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Slf4j
@Configuration
@MapperScan(basePackages = DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class DataSourceConfig {

    static final String PACKAGE = "com.xiaomi.mone.monitor.dao.mapper";
    static final String MAPPER_LOCATION = "classpath*:com/xiaomi/mone/monitor/dao/**/*.xml";

    @NacosValue(value = "${spring.datasource.driverClassName}", autoRefreshed = true)
    private String driverClass;

    @NacosValue(value = "${spring.datasource.default.initialPoolSize}", autoRefreshed = true)
    private Integer defaultInitialPoolSize;

    @NacosValue(value = "${spring.datasource.default.maxPoolSize}", autoRefreshed = true)
    private Integer defaultMaxPoolSize;

    @NacosValue(value = "${spring.datasource.default.minialPoolSize}", autoRefreshed = true)
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
        log.info("DataSourceConfig {} {} {} {}", serverType, dataSourceUrl, dataSourceUserName);

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

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DataSourceConfig.MAPPER_LOCATION));
//        sessionFactory.setPlugins(new Interceptor[]{new CatMybatisInterceptor(dataSourceUrl)});
        sessionFactory.setPlugins(new Interceptor[]{buildPageInterceptor()});
        return sessionFactory.getObject();
    }

    /**
     * 分页插件
     * @return
     */
    private PageInterceptor buildPageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties prop = new Properties();
        prop.setProperty("helperDialect", "mysql");
        pageInterceptor.setProperties(prop);
        return pageInterceptor;
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
