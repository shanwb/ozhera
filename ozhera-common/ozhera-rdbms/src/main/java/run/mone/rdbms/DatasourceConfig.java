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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shanwb
 * @date 2024-01-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasourceConfig implements Serializable {

    private String driverClass;

    private String dataSourceUrl;

    private String dataSourceUserName;

    private String dataSourcePasswd;

    private Integer initialPoolSize = 10;

    private Integer maxPoolSize = 10;

    private Integer minPoolSize = 10;

    private Boolean testConnectionOnCheckIn = true;

    private Boolean testConnectionOnCheckOut = false;

    private String preferredTestQuery = "select 1";

    private int idleConnectionTestPeriod = 180;


}
