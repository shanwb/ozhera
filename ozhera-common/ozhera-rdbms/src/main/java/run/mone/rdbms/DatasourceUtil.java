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

package run.mone.rdbms;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author shanwb
 * @date 2024-01-08
 */
public class DatasourceUtil {

    public static DataSource newDataSource(DatasourceConfig datasourceConfig) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(datasourceConfig.getDriverClass());
        dataSource.setJdbcUrl(datasourceConfig.getDataSourceUrl());
        dataSource.setUser(datasourceConfig.getDataSourceUserName());
        dataSource.setPassword(datasourceConfig.getDataSourcePasswd());

        dataSource.setInitialPoolSize(datasourceConfig.getInitialPoolSize());
        dataSource.setMaxPoolSize(datasourceConfig.getMaxPoolSize());
        dataSource.setMinPoolSize(datasourceConfig.getMinPoolSize());

        dataSource.setTestConnectionOnCheckin(datasourceConfig.getTestConnectionOnCheckIn());
        dataSource.setTestConnectionOnCheckout(datasourceConfig.getTestConnectionOnCheckOut());
        dataSource.setPreferredTestQuery(datasourceConfig.getPreferredTestQuery());
        dataSource.setIdleConnectionTestPeriod(datasourceConfig.getIdleConnectionTestPeriod());

        return dataSource;
    }

}
