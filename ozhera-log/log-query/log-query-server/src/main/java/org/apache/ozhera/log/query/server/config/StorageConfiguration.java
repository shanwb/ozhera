/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ozhera.log.query.server.config;

import com.xiaomi.youpin.docean.anno.Component;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.common.Config;
import org.apache.ozhera.log.query.core.adapter.MySqlAdapter;
import org.apache.ozhera.log.query.core.adapter.StorageAdapterFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Storage configuration initializer
 */
@Slf4j
@Component
public class StorageConfiguration {

    private static final Long DEFAULT_CLUSTER_ID = 1L;

    @PostConstruct
    public void init() {
        Config config = Config.ins();
        String defaultStorage = config.get("log.query.default.storage", "mysql");

        log.info("Initializing storage configuration, default storage: {}", defaultStorage);

        if ("mysql".equalsIgnoreCase(defaultStorage)) {
            initMySqlDataSource(config);
        }
    }

    private void initMySqlDataSource(Config config) {
        String addr = config.get("mysql.log.addr", "");
        String database = config.get("mysql.log.database", "hera_log");
        String user = config.get("mysql.log.user", "root");
        String pwd = config.get("mysql.log.pwd", "");
        int poolSize = Integer.parseInt(config.get("mysql.log.pool.size", "10"));

        if (addr == null || addr.isEmpty()) {
            log.warn("MySQL address not configured, skipping MySQL DataSource initialization");
            return;
        }

        try {
            String jdbcUrl = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
                    addr, database);

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(user);
            hikariConfig.setPassword(pwd);
            hikariConfig.setMaximumPoolSize(poolSize);
            hikariConfig.setMinimumIdle(2);
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setIdleTimeout(600000);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.setPoolName("log-query-mysql-pool");

            DataSource dataSource = new HikariDataSource(hikariConfig);

            // Register to MySqlAdapter
            MySqlAdapter mysqlAdapter = (MySqlAdapter) StorageAdapterFactory.getAdapter("mysql");
            if (mysqlAdapter != null) {
                mysqlAdapter.registerDataSource(DEFAULT_CLUSTER_ID, dataSource);
                log.info("MySQL DataSource initialized successfully: {}/{}", addr, database);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MySQL DataSource", e);
        }
    }
}
