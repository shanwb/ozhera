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
package org.apache.ozhera.log.query.server.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.service.LogQueryService;
import org.apache.ozhera.log.query.common.Result;

import javax.annotation.Resource;

/**
 * Unified log query controller
 */
@Slf4j
@Controller
public class LogQueryController {

    @Resource
    private LogQueryService logQueryService;

    /**
     * Query logs with unified query model
     *
     * @param query unified log query
     * @return query result
     */
    @RequestMapping(path = "/api/v1/log/query", method = "post")
    public Result<UnifiedLogResult> query(UnifiedLogQuery query) {
        try {
            log.info("LogQueryController.query, storeId: {}", query.getStoreId());
            UnifiedLogResult result = logQueryService.query(query);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Log query error", e);
            return Result.fail("Query failed: " + e.getMessage());
        }
    }

    /**
     * Query log context
     *
     * @param storeId    store ID
     * @param ip         IP address
     * @param fileName   file name
     * @param lineNumber line number
     * @param timestamp  timestamp
     * @param type       context type (0-both, 1-after, 2-before)
     * @param pageSize   page size
     * @return context logs
     */
    @RequestMapping(path = "/api/v1/log/context", method = "get")
    public Result<UnifiedLogResult> queryContext(
            @RequestParam("storeId") Long storeId,
            @RequestParam("ip") String ip,
            @RequestParam("fileName") String fileName,
            @RequestParam("lineNumber") Long lineNumber,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("type") Integer type,
            @RequestParam("pageSize") Integer pageSize) {
        try {
            log.info("LogQueryController.queryContext, storeId: {}, ip: {}", storeId, ip);

            LogContextQuery contextQuery = LogContextQuery.builder()
                    .storeId(storeId)
                    .ip(ip)
                    .fileName(fileName)
                    .lineNumber(lineNumber)
                    .timestamp(timestamp)
                    .type(type != null ? type : 0)
                    .pageSize(pageSize != null ? pageSize : 20)
                    .build();

            UnifiedLogResult result = logQueryService.queryContext(contextQuery);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Log context query error", e);
            return Result.fail("Context query failed: " + e.getMessage());
        }
    }

    /**
     * Execute aggregation/statistics query
     *
     * @param query unified log query with aggregation spec
     * @return aggregation result
     */
    @RequestMapping(path = "/api/v1/log/aggregate", method = "post")
    public Result<AggregationResult> aggregate(UnifiedLogQuery query) {
        try {
            log.info("LogQueryController.aggregate, storeId: {}", query.getStoreId());
            AggregationResult result = logQueryService.aggregate(query);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Log aggregation error", e);
            return Result.fail("Aggregation failed: " + e.getMessage());
        }
    }

    /**
     * Count logs matching the query
     *
     * @param query unified log query
     * @return count result
     */
    @RequestMapping(path = "/api/v1/log/count", method = "post")
    public Result<Long> count(UnifiedLogQuery query) {
        try {
            log.info("LogQueryController.count, storeId: {}", query.getStoreId());
            long count = logQueryService.count(query);
            return Result.success(count);
        } catch (Exception e) {
            log.error("Log count error", e);
            return Result.fail("Count failed: " + e.getMessage());
        }
    }
}
