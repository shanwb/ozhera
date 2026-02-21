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
package org.apache.ozhera.log.query.api.service;

import org.apache.ozhera.log.query.api.dto.*;

/**
 * Unified log query service interface
 */
public interface LogQueryService {

    /**
     * Query logs with unified query model
     *
     * @param query unified log query
     * @return query result
     */
    UnifiedLogResult query(UnifiedLogQuery query);

    /**
     * Query log context (before and after a specific log line)
     *
     * @param contextQuery context query parameters
     * @return context logs
     */
    UnifiedLogResult queryContext(LogContextQuery contextQuery);

    /**
     * Execute aggregation/statistics query
     *
     * @param query unified log query with aggregation spec
     * @return aggregation result
     */
    AggregationResult aggregate(UnifiedLogQuery query);

    /**
     * Count logs matching the query
     *
     * @param query unified log query
     * @return count of matching logs
     */
    long count(UnifiedLogQuery query);

    /**
     * Export logs to file
     *
     * @param query unified log query
     * @param exportPath export file path
     */
    void export(UnifiedLogQuery query, String exportPath);
}
