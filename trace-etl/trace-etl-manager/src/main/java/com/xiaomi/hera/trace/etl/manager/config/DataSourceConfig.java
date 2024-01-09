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

package com.xiaomi.hera.trace.etl.manager.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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

    static final String PACKAGE = "com.xiaomi.hera.trace.etl.mapper";
    static final String MAPPER_LOCATION = "classpath*:sqlmappers/*.xml";

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.default.initialPoolSize}")
    private Integer defaultInitialPoolSize;

    @Value("${spring.datasource.default.maxPoolSize}")
    private Integer defaultMaxPoolSize;

    @Value("${spring.datasource.default.minialPoolSize}")
    private Integer defaultMinPoolSize;

    @NacosValue("${spring.datasource.username}")
    private String dataSourceUserName;

    @NacosValue("${spring.datasource.url}")
    private String dataSourceUrl;

    @NacosValue("${spring.datasource.password}")
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
        sessionFactory.setPlugins(new Interceptor[]{buildPageInterceptor()});
        return sessionFactory.getObject();
    }

    /**
     * page plugin
     *
     * @return
     */
    private PageInterceptor buildPageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties prop = new Properties();
        prop.setProperty("helperDialect", "mysql");
        pageInterceptor.setProperties(prop);
        return pageInterceptor;
    }

}
