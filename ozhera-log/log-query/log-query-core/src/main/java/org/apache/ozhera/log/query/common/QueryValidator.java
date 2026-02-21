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
package org.apache.ozhera.log.query.common;

/**
 * Query validation utilities
 */
public class QueryValidator {

    private static final long MAX_TIME_RANGE_MS = 7 * 24 * 60 * 60 * 1000L; // 7 days
    private static final int MAX_PAGE_SIZE = 10000;
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Validate and normalize time range
     *
     * @param startTime start time in milliseconds
     * @param endTime   end time in milliseconds
     * @return normalized time range [startTime, endTime]
     */
    public static long[] validateTimeRange(Long startTime, Long endTime) {
        long now = System.currentTimeMillis();

        if (startTime == null) {
            startTime = now - 60 * 60 * 1000L; // default 1 hour ago
        }
        if (endTime == null) {
            endTime = now;
        }

        // Swap if reversed
        if (startTime > endTime) {
            long temp = startTime;
            startTime = endTime;
            endTime = temp;
        }

        // Limit time range
        if (endTime - startTime > MAX_TIME_RANGE_MS) {
            startTime = endTime - MAX_TIME_RANGE_MS;
        }

        return new long[]{startTime, endTime};
    }

    /**
     * Validate and normalize page size
     *
     * @param pageSize requested page size
     * @return normalized page size
     */
    public static int validatePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    /**
     * Validate and normalize page number
     *
     * @param page requested page number
     * @return normalized page number (minimum 1)
     */
    public static int validatePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    /**
     * Check if full text search contains potentially dangerous patterns
     *
     * @param fullTextSearch search text
     * @return true if safe, false if potentially dangerous
     */
    public static boolean isSearchTextSafe(String fullTextSearch) {
        if (fullTextSearch == null || fullTextSearch.isEmpty()) {
            return true;
        }

        // Check for SQL injection patterns (for SQL-based adapters)
        String lowerCase = fullTextSearch.toLowerCase();
        String[] dangerousPatterns = {
                "drop ", "delete ", "truncate ", "update ", "insert ",
                "--", "/*", "*/", "xp_", "exec ", "execute "
        };

        for (String pattern : dangerousPatterns) {
            if (lowerCase.contains(pattern)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sanitize search text for safe use
     *
     * @param fullTextSearch original search text
     * @return sanitized search text
     */
    public static String sanitizeSearchText(String fullTextSearch) {
        if (fullTextSearch == null) {
            return null;
        }

        // Remove potentially dangerous characters for SQL
        return fullTextSearch
                .replace("'", "''")
                .replace("\\", "\\\\")
                .replace(";", "")
                .replace("--", "")
                .trim();
    }
}
