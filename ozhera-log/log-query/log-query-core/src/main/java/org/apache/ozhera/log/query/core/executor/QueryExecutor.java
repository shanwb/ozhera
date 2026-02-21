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
package org.apache.ozhera.log.query.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;
import org.apache.ozhera.log.query.core.adapter.StorageAdapter;
import org.apache.ozhera.log.query.core.adapter.StorageAdapterFactory;

import java.util.List;

/**
 * Query executor that routes queries to appropriate storage adapters
 */
@Slf4j
public class QueryExecutor {

    /**
     * Execute log query
     *
     * @param query       unified log query
     * @param storageInfo storage connection info
     * @param keyList     list of fields to retrieve
     * @return query result
     */
    public UnifiedLogResult executeQuery(UnifiedLogQuery query, StorageInfo storageInfo, List<String> keyList) {
        StorageAdapter adapter = getAdapter(storageInfo.getStorageType());
        if (adapter == null) {
            log.error("No adapter found for storage type: {}", storageInfo.getStorageType());
            return UnifiedLogResult.empty();
        }

        log.info("Executing query with adapter: {}, storeId: {}", adapter.getStorageType(), query.getStoreId());
        return adapter.query(query, storageInfo, keyList);
    }

    /**
     * Execute context query
     *
     * @param contextQuery context query parameters
     * @param storageInfo  storage connection info
     * @param keyList      list of fields to retrieve
     * @return context logs
     */
    public UnifiedLogResult executeContextQuery(LogContextQuery contextQuery, StorageInfo storageInfo, List<String> keyList) {
        StorageAdapter adapter = getAdapter(storageInfo.getStorageType());
        if (adapter == null) {
            log.error("No adapter found for storage type: {}", storageInfo.getStorageType());
            return UnifiedLogResult.empty();
        }

        return adapter.queryContext(contextQuery, storageInfo, keyList);
    }

    /**
     * Execute aggregation query
     *
     * @param query       unified log query with aggregation spec
     * @param storageInfo storage connection info
     * @return aggregation result
     */
    public AggregationResult executeAggregation(UnifiedLogQuery query, StorageInfo storageInfo) {
        StorageAdapter adapter = getAdapter(storageInfo.getStorageType());
        if (adapter == null) {
            log.error("No adapter found for storage type: {}", storageInfo.getStorageType());
            return new AggregationResult();
        }

        if (!adapter.supportsAggregation()) {
            log.warn("Adapter {} does not support aggregation", adapter.getStorageType());
            return new AggregationResult();
        }

        return adapter.aggregate(query, storageInfo);
    }

    /**
     * Execute count query
     *
     * @param query       unified log query
     * @param storageInfo storage connection info
     * @return count of matching logs
     */
    public long executeCount(UnifiedLogQuery query, StorageInfo storageInfo) {
        StorageAdapter adapter = getAdapter(storageInfo.getStorageType());
        if (adapter == null) {
            log.error("No adapter found for storage type: {}", storageInfo.getStorageType());
            return 0L;
        }

        return adapter.count(query, storageInfo);
    }

    private StorageAdapter getAdapter(LogStorageTypeEnum storageType) {
        return StorageAdapterFactory.getAdapter(storageType);
    }
}
