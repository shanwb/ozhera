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
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.query.api.enums.FullTextModeEnum;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Unified log query request DTO
 * <p>
 * Supports multiple time formats for startTime and endTime:
 * <ul>
 *   <li>Millisecond timestamp (Long or String): 1738368000000</li>
 *   <li>ISO 8601 format: "2024-01-01T00:00:00Z" or "2024-01-01T00:00:00+08:00"</li>
 *   <li>Date time format: "2024-01-01 00:00:00" or "2024-01-01T00:00:00"</li>
 *   <li>Date only format: "2024-01-01" (starts at 00:00:00)</li>
 * </ul>
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedLogQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    // Common date time formats
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ISO_INSTANT,                          // 2024-01-01T00:00:00Z
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,                 // 2024-01-01T00:00:00+08:00
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,                  // 2024-01-01T00:00:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),     // 2024-01-01 00:00:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),              // 2024-01-01
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),     // 2024/01/01 00:00:00
            DateTimeFormatter.ofPattern("yyyy/MM/dd")               // 2024/01/01
    };

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
     * Start time (required).
     * Supports multiple formats:
     * <ul>
     *   <li>Millisecond timestamp (Long): 1738368000000</li>
     *   <li>Millisecond timestamp (String): "1738368000000"</li>
     *   <li>ISO 8601: "2024-01-01T00:00:00Z"</li>
     *   <li>Date time: "2024-01-01 00:00:00"</li>
     *   <li>Date only: "2024-01-01"</li>
     * </ul>
     */
    private Object startTime;

    /**
     * End time (required).
     * Supports the same formats as startTime.
     */
    private Object endTime;

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

    /**
     * Get start time as milliseconds timestamp.
     * Supports multiple input formats and converts them to milliseconds.
     *
     * @return start time in milliseconds, or null if not set
     */
    public Long getStartTimeMs() {
        return parseTime(startTime);
    }

    /**
     * Get end time as milliseconds timestamp.
     * Supports multiple input formats and converts them to milliseconds.
     *
     * @return end time in milliseconds, or null if not set
     */
    public Long getEndTimeMs() {
        return parseTime(endTime);
    }

    /**
     * Parse time value to milliseconds timestamp.
     * Supports: Long, Integer, String (timestamp or date format)
     *
     * @param timeValue the time value to parse
     * @return milliseconds timestamp, or null if input is null/invalid
     */
    private Long parseTime(Object timeValue) {
        if (timeValue == null) {
            return null;
        }

        // Handle Number types (Long, Integer, etc.)
        if (timeValue instanceof Number) {
            return ((Number) timeValue).longValue();
        }

        // Handle String type
        if (timeValue instanceof String) {
            String strValue = ((String) timeValue).trim();
            if (strValue.isEmpty()) {
                return null;
            }

            // Try parsing as numeric timestamp
            if (strValue.matches("\\d+")) {
                try {
                    return Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse numeric timestamp: {}", strValue);
                }
            }

            // Try parsing as date string with various formats
            return parseDateString(strValue);
        }

        log.warn("Unsupported time value type: {}", timeValue.getClass().getName());
        return null;
    }

    /**
     * Parse date string to milliseconds timestamp.
     * Tries multiple common date formats.
     *
     * @param dateStr the date string to parse
     * @return milliseconds timestamp, or null if parsing fails
     */
    private Long parseDateString(String dateStr) {
        // Try ISO instant format first (with timezone)
        try {
            Instant instant = Instant.parse(dateStr);
            return instant.toEpochMilli();
        } catch (Exception ignored) {
        }

        // Try various local date time formats
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                if (formatter.equals(DateTimeFormatter.ISO_INSTANT) ||
                    formatter.equals(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) {
                    continue; // Already tried above
                }

                // Handle date-only format
                if (dateStr.length() == 10 && dateStr.contains("-") && !dateStr.contains(":")) {
                    LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }

                // Handle date-only format with slash
                if (dateStr.length() == 10 && dateStr.contains("/") && !dateStr.contains(":")) {
                    LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }

                // Try parsing as LocalDateTime
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
                    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                } catch (Exception e) {
                    // Try as LocalDate
                    try {
                        LocalDate localDate = LocalDate.parse(dateStr, formatter);
                        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    } catch (Exception ignored2) {
                    }
                }
            } catch (Exception ignored) {
            }
        }

        log.warn("Failed to parse date string: {}. Supported formats: " +
                "millisecond timestamp, ISO 8601 (2024-01-01T00:00:00Z), " +
                "date time (2024-01-01 00:00:00), date only (2024-01-01)", dateStr);
        return null;
    }

    /**
     * Validate the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (storeId == null) {
            return false;
        }
        Long start = getStartTimeMs();
        Long end = getEndTimeMs();
        return start != null && end != null && start <= end;
    }
}
