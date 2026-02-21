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
 * Aggregation specification for statistics queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Aggregation type
     */
    private AggregationType type;

    /**
     * Field to aggregate on
     */
    private String field;

    /**
     * Interval for date histogram (e.g., "1h", "1d", "30s")
     */
    private String interval;

    /**
     * Time zone for date histogram
     */
    @Builder.Default
    private String timeZone = "+08:00";

    /**
     * Number of top terms to return (for terms aggregation)
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * Minimum document count for buckets
     */
    @Builder.Default
    private Long minDocCount = 0L;

    /**
     * Sub-aggregations
     */
    private List<AggregationSpec> subAggregations;

    public enum AggregationType {
        DATE_HISTOGRAM,
        TERMS,
        COUNT,
        SUM,
        AVG,
        MIN,
        MAX,
        CARDINALITY,
        PERCENTILES
    }

    public static AggregationSpec dateHistogram(String field, String interval) {
        return AggregationSpec.builder()
                .type(AggregationType.DATE_HISTOGRAM)
                .field(field)
                .interval(interval)
                .build();
    }

    public static AggregationSpec terms(String field, int size) {
        return AggregationSpec.builder()
                .type(AggregationType.TERMS)
                .field(field)
                .size(size)
                .build();
    }

    public static AggregationSpec count() {
        return AggregationSpec.builder()
                .type(AggregationType.COUNT)
                .build();
    }
}
