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
package org.apache.ozhera.log.query.core.service.impl;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;
import org.apache.ozhera.log.query.api.service.LogQueryService;
import org.apache.ozhera.log.query.core.executor.QueryExecutor;

import java.util.List;

/**
 * Unified log query service implementation
 */
@Slf4j
@Service
public class LogQueryServiceImpl implements LogQueryService {

    private final QueryExecutor queryExecutor;

    // Storage info provider interface - to be implemented by integrating module
    private StorageInfoProvider storageInfoProvider;

    public LogQueryServiceImpl() {
        this.queryExecutor = new QueryExecutor();
    }

    public void setStorageInfoProvider(StorageInfoProvider storageInfoProvider) {
        this.storageInfoProvider = storageInfoProvider;
    }

    @Override
    public UnifiedLogResult query(UnifiedLogQuery query) {
        log.info("LogQueryService.query, storeId: {}, fullTextSearch: {}", query.getStoreId(), query.getFullTextSearch());

        // Validate query
        if (!validateQuery(query)) {
            log.warn("Invalid query parameters: {}", query);
            return UnifiedLogResult.empty();
        }

        // Get storage info
        StorageInfo storageInfo = getStorageInfo(query.getStoreId());
        if (storageInfo == null) {
            log.error("Storage info not found for storeId: {}", query.getStoreId());
            return UnifiedLogResult.empty();
        }

        // Get key list
        List<String> keyList = getKeyList(query.getStoreId());

        // Execute query
        return queryExecutor.executeQuery(query, storageInfo, keyList);
    }

    @Override
    public UnifiedLogResult queryContext(LogContextQuery contextQuery) {
        log.info("LogQueryService.queryContext, storeId: {}, ip: {}, fileName: {}",
                contextQuery.getStoreId(), contextQuery.getIp(), contextQuery.getFileName());

        // Validate context query
        if (!validateContextQuery(contextQuery)) {
            log.warn("Invalid context query parameters: {}", contextQuery);
            return UnifiedLogResult.empty();
        }

        // Get storage info
        StorageInfo storageInfo = getStorageInfo(contextQuery.getStoreId());
        if (storageInfo == null) {
            log.error("Storage info not found for storeId: {}", contextQuery.getStoreId());
            return UnifiedLogResult.empty();
        }

        // Get key list
        List<String> keyList = getKeyList(contextQuery.getStoreId());

        // Execute context query
        return queryExecutor.executeContextQuery(contextQuery, storageInfo, keyList);
    }

    @Override
    public AggregationResult aggregate(UnifiedLogQuery query) {
        log.info("LogQueryService.aggregate, storeId: {}", query.getStoreId());

        // Validate query
        if (!validateQuery(query)) {
            log.warn("Invalid query parameters: {}", query);
            return new AggregationResult();
        }

        // Get storage info
        StorageInfo storageInfo = getStorageInfo(query.getStoreId());
        if (storageInfo == null) {
            log.error("Storage info not found for storeId: {}", query.getStoreId());
            return new AggregationResult();
        }

        // Execute aggregation
        return queryExecutor.executeAggregation(query, storageInfo);
    }

    @Override
    public long count(UnifiedLogQuery query) {
        log.info("LogQueryService.count, storeId: {}", query.getStoreId());

        // Validate query
        if (!validateQuery(query)) {
            log.warn("Invalid query parameters: {}", query);
            return 0L;
        }

        // Get storage info
        StorageInfo storageInfo = getStorageInfo(query.getStoreId());
        if (storageInfo == null) {
            log.error("Storage info not found for storeId: {}", query.getStoreId());
            return 0L;
        }

        // Execute count
        return queryExecutor.executeCount(query, storageInfo);
    }

    @Override
    public void export(UnifiedLogQuery query, String exportPath) {
        log.info("LogQueryService.export, storeId: {}, exportPath: {}", query.getStoreId(), exportPath);
        // Export implementation will be added in Phase 2
        throw new UnsupportedOperationException("Export not implemented yet");
    }

    // ==================== Private Helper Methods ====================

    private boolean validateQuery(UnifiedLogQuery query) {
        if (query == null) {
            return false;
        }
        if (query.getStoreId() == null) {
            return false;
        }
        if (query.getStartTimeMs() == null || query.getEndTimeMs() == null) {
            return false;
        }
        if (query.getStartTimeMs() > query.getEndTimeMs()) {
            return false;
        }
        return true;
    }

    private boolean validateContextQuery(LogContextQuery query) {
        if (query == null) {
            return false;
        }
        if (query.getStoreId() == null) {
            return false;
        }
        if (query.getIp() == null || query.getIp().isEmpty()) {
            return false;
        }
        if (query.getFileName() == null || query.getFileName().isEmpty()) {
            return false;
        }
        if (query.getLineNumber() == null) {
            return false;
        }
        if (query.getTimestamp() == null || query.getTimestamp().isEmpty()) {
            return false;
        }
        return true;
    }

    private static final String MYSQL_TABLE_PREFIX = "hera_log_mysql_table";

    private StorageInfo getStorageInfo(Long storeId) {
        if (storageInfoProvider != null) {
            return storageInfoProvider.getStorageInfo(storeId);
        }
        // Default implementation - use MySQL with default cluster ID
        // Table name format: hera_log_mysql_table_{clusterId}_{storeId}
        log.warn("StorageInfoProvider not set, returning default MySQL storage info");
        Long clusterId = 1L;
        String tableName = String.format("%s_%s_%s", MYSQL_TABLE_PREFIX, clusterId, storeId);
        return StorageInfo.builder()
                .clusterId(clusterId)
                .storageType(LogStorageTypeEnum.MYSQL)
                .indexName(tableName)
                .build();
    }

    private List<String> getKeyList(Long storeId) {
        if (storageInfoProvider != null) {
            return storageInfoProvider.getKeyList(storeId);
        }
        // Default key list
        return List.of("timestamp", "message", "level", "traceId", "logip", "filename", "linenumber");
    }

    /**
     * Interface for providing storage info - to be implemented by integrating module
     */
    public interface StorageInfoProvider {
        StorageInfo getStorageInfo(Long storeId);
        List<String> getKeyList(Long storeId);
    }
}
