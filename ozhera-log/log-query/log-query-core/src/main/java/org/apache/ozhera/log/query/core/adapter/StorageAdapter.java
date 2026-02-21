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
package org.apache.ozhera.log.query.core.adapter;

import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;

/**
 * Storage adapter interface for different storage backends
 */
public interface StorageAdapter {

    /**
     * Get the storage type this adapter supports
     *
     * @return storage type
     */
    LogStorageTypeEnum getStorageType();

    /**
     * Execute log query
     *
     * @param query       unified log query
     * @param storageInfo storage connection info
     * @param keyList     list of fields to retrieve
     * @return query result
     */
    UnifiedLogResult query(UnifiedLogQuery query, StorageInfo storageInfo, java.util.List<String> keyList);

    /**
     * Execute log context query
     *
     * @param contextQuery context query parameters
     * @param storageInfo  storage connection info
     * @param keyList      list of fields to retrieve
     * @return context logs
     */
    UnifiedLogResult queryContext(LogContextQuery contextQuery, StorageInfo storageInfo, java.util.List<String> keyList);

    /**
     * Execute aggregation query
     *
     * @param query       unified log query with aggregation spec
     * @param storageInfo storage connection info
     * @return aggregation result
     */
    AggregationResult aggregate(UnifiedLogQuery query, StorageInfo storageInfo);

    /**
     * Count logs matching the query
     *
     * @param query       unified log query
     * @param storageInfo storage connection info
     * @return count of matching logs
     */
    long count(UnifiedLogQuery query, StorageInfo storageInfo);

    /**
     * Check if this adapter supports highlight
     *
     * @return true if highlight is supported
     */
    default boolean supportsHighlight() {
        return false;
    }

    /**
     * Check if this adapter supports searchAfter pagination
     *
     * @return true if searchAfter is supported
     */
    default boolean supportsSearchAfter() {
        return false;
    }

    /**
     * Check if this adapter supports full text search
     *
     * @return true if full text search is supported
     */
    default boolean supportsFullTextSearch() {
        return false;
    }

    /**
     * Check if this adapter supports aggregation
     *
     * @return true if aggregation is supported
     */
    default boolean supportsAggregation() {
        return false;
    }
}
