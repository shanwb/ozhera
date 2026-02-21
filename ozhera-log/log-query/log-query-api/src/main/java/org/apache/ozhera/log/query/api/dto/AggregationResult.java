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
 * Aggregation/Statistics result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Aggregation name
     */
    private String name;

    /**
     * Timestamps for date histogram
     */
    private List<String> timestamps;

    /**
     * Counts for each bucket
     */
    private List<Long> counts;

    /**
     * Total count
     */
    private Long totalCount;

    /**
     * Bucket results for terms aggregation
     */
    private List<BucketResult> buckets;

    /**
     * Query execution time in milliseconds
     */
    private Long queryTimeMs;

    /**
     * Debug info (query DSL, SQL, etc.)
     */
    private String debugInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BucketResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private String key;
        private Long count;
        private List<BucketResult> subBuckets;
    }

    public void calculateTotalCount() {
        if (counts != null && !counts.isEmpty()) {
            this.totalCount = counts.stream().mapToLong(Long::longValue).sum();
        }
    }
}
