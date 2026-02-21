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
package org.apache.ozhera.log.query.core.converter;

import org.apache.ozhera.log.query.api.dto.UnifiedLogQuery;

/**
 * Converter for legacy LogQuery to UnifiedLogQuery
 * This helps migrate from log-manager's LogQuery to the new unified model
 */
public class LogQueryConverter {

    /**
     * Convert legacy log-manager LogQuery parameters to UnifiedLogQuery
     *
     * @param logstore      log store name
     * @param storeId       store ID
     * @param tail          tail names (comma separated)
     * @param startTime     start time in milliseconds
     * @param endTime       end time in milliseconds
     * @param fullTextSearch full text search keyword
     * @param sortKey       sort field
     * @param asc           ascending order
     * @param page          page number
     * @param pageSize      page size
     * @return UnifiedLogQuery
     */
    public static UnifiedLogQuery fromLegacy(
            String logstore,
            Long storeId,
            String tail,
            Long startTime,
            Long endTime,
            String fullTextSearch,
            String sortKey,
            Boolean asc,
            Integer page,
            Integer pageSize) {

        return UnifiedLogQuery.builder()
                .storeName(logstore)
                .storeId(storeId)
                .tailNames(tail)
                .startTime(startTime)
                .endTime(endTime)
                .fullTextSearch(fullTextSearch)
                .sortField(sortKey != null ? sortKey : "timestamp")
                .ascending(asc != null ? asc : false)
                .page(page != null ? page : 1)
                .pageSize(pageSize != null ? pageSize : 100)
                .enableHighlight(true)
                .build();
    }

    /**
     * Convert with search after support for deep pagination
     */
    public static UnifiedLogQuery fromLegacyWithSearchAfter(
            String logstore,
            Long storeId,
            String tail,
            Long startTime,
            Long endTime,
            String fullTextSearch,
            String sortKey,
            Boolean asc,
            Integer page,
            Integer pageSize,
            Object[] searchAfter,
            Boolean isDownload) {

        UnifiedLogQuery query = fromLegacy(logstore, storeId, tail, startTime, endTime,
                fullTextSearch, sortKey, asc, page, pageSize);
        query.setSearchAfter(searchAfter);
        query.setIsExport(isDownload != null ? isDownload : false);

        if (isDownload != null && isDownload) {
            query.setEnableHighlight(false);
        }

        return query;
    }
}
