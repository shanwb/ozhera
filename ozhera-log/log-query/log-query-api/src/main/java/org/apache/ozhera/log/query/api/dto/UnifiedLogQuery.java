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
import org.apache.ozhera.log.query.api.enums.FullTextModeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Unified log query request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedLogQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== Basic Identifiers ====================

    /**
     * Store ID (required)
     */
    private Long storeId;

    /**
     * Store name
     */
    private String storeName;

    /**
     * Tail IDs for filtering
     */
    private List<Long> tailIds;

    /**
     * Tail names for filtering (comma separated)
     */
    private String tailNames;

    // ==================== Time Range ====================

    /**
     * Start time in milliseconds (required)
     */
    private Long startTime;

    /**
     * End time in milliseconds (required)
     */
    private Long endTime;

    // ==================== Full Text Search ====================

    /**
     * Full text search keyword
     */
    private String fullTextSearch;

    /**
     * Full text search mode
     */
    @Builder.Default
    private FullTextModeEnum fullTextMode = FullTextModeEnum.MATCH;

    // ==================== Field Conditions ====================

    /**
     * Field filter conditions
     */
    private List<FieldCondition> conditions;

    // ==================== Pagination & Sorting ====================

    /**
     * Sort field, default is "timestamp"
     */
    @Builder.Default
    private String sortField = "timestamp";

    /**
     * Sort ascending, default is false (descending)
     */
    @Builder.Default
    private Boolean ascending = false;

    /**
     * Page number, starting from 1
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * Page size
     */
    @Builder.Default
    private Integer pageSize = 100;

    /**
     * Search after values for deep pagination (ES specific)
     */
    private Object[] searchAfter;

    // ==================== Advanced Features ====================

    /**
     * Enable highlight
     */
    @Builder.Default
    private Boolean enableHighlight = true;

    /**
     * Fields to highlight (null means all fields)
     */
    private List<String> highlightFields;

    /**
     * Whether this is a download/export request
     */
    @Builder.Default
    private Boolean isExport = false;

    // ==================== Aggregation ====================

    /**
     * Aggregation specification
     */
    private AggregationSpec aggregation;

    // ==================== Helper Methods ====================

    public int getOffset() {
        return (page - 1) * pageSize;
    }

    public boolean hasFullTextSearch() {
        return fullTextSearch != null && !fullTextSearch.trim().isEmpty();
    }

    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }

    public boolean hasTailFilter() {
        return (tailIds != null && !tailIds.isEmpty())
            || (tailNames != null && !tailNames.trim().isEmpty());
    }
}
