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
package org.apache.ozhera.log.query.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Unified log query result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedLogResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log data list
     */
    private List<LogDataItem> logs;

    /**
     * Total count of matching logs
     */
    private Long total;

    /**
     * Current page number
     */
    private Integer page;

    /**
     * Page size
     */
    private Integer pageSize;

    /**
     * Sort values of the last item (for searchAfter pagination)
     */
    private Object[] lastSortValues;

    /**
     * Whether there are more results
     */
    private Boolean hasMore;

    /**
     * Query execution time in milliseconds
     */
    private Long queryTimeMs;

    /**
     * Storage type used for this query
     */
    private String storageType;

    /**
     * Debug info (query DSL, SQL, etc.)
     */
    private String debugInfo;

    public static UnifiedLogResult empty() {
        return UnifiedLogResult.builder()
                .logs(List.of())
                .total(0L)
                .hasMore(false)
                .build();
    }

    public int getLogCount() {
        return logs != null ? logs.size() : 0;
    }
}
