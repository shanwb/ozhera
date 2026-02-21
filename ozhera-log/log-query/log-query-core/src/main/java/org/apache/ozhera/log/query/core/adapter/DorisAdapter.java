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

import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.parse.LogParser;
import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;
import org.apache.ozhera.log.query.api.enums.OperatorEnum;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Doris storage adapter implementation
 */
@Slf4j
@AutoService(StorageAdapter.class)
public class DorisAdapter implements StorageAdapter {

    private static final Set<String> HIDDEN_FIELDS = Set.of("mqtag", "mqtopic", "logstore", "linenumber", "filename");

    private final Map<Long, DataSource> dataSourceCache = new HashMap<>();

    @Override
    public LogStorageTypeEnum getStorageType() {
        return LogStorageTypeEnum.DORIS;
    }

    @Override
    public UnifiedLogResult query(UnifiedLogQuery query, StorageInfo storageInfo, List<String> keyList) {
        long startTime = System.currentTimeMillis();
        try {
            DataSource dataSource = getDataSource(storageInfo);
            if (dataSource == null) {
                log.error("DataSource not found for clusterId: {}", storageInfo.getClusterId());
                return UnifiedLogResult.empty();
            }

            String sql = buildQuerySql(query, storageInfo.getIndexName());
            log.info("Doris query SQL: {}", sql);

            List<Map<String, Object>> results = executeQuery(dataSource, sql);
            List<LogDataItem> logDataList = transformResults(results, keyList);

            return UnifiedLogResult.builder()
                    .logs(logDataList)
                    .total((long) logDataList.size())
                    .page(query.getPage())
                    .pageSize(query.getPageSize())
                    .queryTimeMs(System.currentTimeMillis() - startTime)
                    .storageType(LogStorageTypeEnum.DORIS.getCode())
                    .debugInfo(sql)
                    .build();
        } catch (Exception e) {
            log.error("Doris query error, query: {}", query, e);
            return UnifiedLogResult.empty();
        }
    }

    @Override
    public UnifiedLogResult queryContext(LogContextQuery contextQuery, StorageInfo storageInfo, List<String> keyList) {
        // Doris context query - simplified implementation
        log.warn("Doris context query not fully supported, returning empty result");
        return UnifiedLogResult.empty();
    }

    @Override
    public AggregationResult aggregate(UnifiedLogQuery query, StorageInfo storageInfo) {
        long startTime = System.currentTimeMillis();
        try {
            DataSource dataSource = getDataSource(storageInfo);
            if (dataSource == null) {
                return new AggregationResult();
            }

            String baseSql = buildBaseQuerySql(query, storageInfo.getIndexName());
            String statSql = buildStatisticsSql(baseSql);
            log.info("Doris stat SQL: {}", statSql);

            List<String> timestamps = new ArrayList<>();
            List<Long> counts = executeStatQuery(dataSource, statSql, timestamps);

            AggregationResult result = AggregationResult.builder()
                    .timestamps(timestamps)
                    .counts(counts)
                    .queryTimeMs(System.currentTimeMillis() - startTime)
                    .debugInfo(statSql)
                    .build();
            result.calculateTotalCount();

            return result;
        } catch (Exception e) {
            log.error("Doris aggregation error, query: {}", query, e);
            return new AggregationResult();
        }
    }

    @Override
    public long count(UnifiedLogQuery query, StorageInfo storageInfo) {
        try {
            DataSource dataSource = getDataSource(storageInfo);
            if (dataSource == null) {
                return 0L;
            }

            String countSql = buildCountSql(query, storageInfo.getIndexName());
            log.info("Doris count SQL: {}", countSql);

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0L;
        } catch (Exception e) {
            log.error("Doris count error, query: {}", query, e);
            return 0L;
        }
    }

    @Override
    public boolean supportsAggregation() {
        return true;
    }

    // ==================== Private Helper Methods ====================

    private DataSource getDataSource(StorageInfo storageInfo) {
        return dataSourceCache.get(storageInfo.getClusterId());
    }

    public void registerDataSource(Long clusterId, DataSource dataSource) {
        dataSourceCache.put(clusterId, dataSource);
    }

    private String buildQuerySql(UnifiedLogQuery query, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName);
        sql.append(" WHERE ").append(buildWhereClause(query));

        // Order by
        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            sql.append(" ORDER BY ").append(query.getSortField());
            sql.append(query.getAscending() ? " ASC" : " DESC");
        }

        // Limit
        sql.append(" LIMIT ").append(query.getOffset()).append(", ").append(query.getPageSize());

        return sql.toString();
    }

    private String buildBaseQuerySql(UnifiedLogQuery query, String tableName) {
        return "SELECT * FROM " + tableName + " WHERE " + buildWhereClause(query);
    }

    private String buildCountSql(UnifiedLogQuery query, String tableName) {
        return "SELECT COUNT(*) FROM " + tableName + " WHERE " + buildWhereClause(query);
    }

    private String buildWhereClause(UnifiedLogQuery query) {
        List<String> conditions = new ArrayList<>();

        // Time range
        conditions.add(String.format("timestamp >= %d AND timestamp <= %d",
                query.getStartTime(), query.getEndTime()));

        // Tail filter
        if (query.hasTailFilter()) {
            if (query.getTailIds() != null && !query.getTailIds().isEmpty()) {
                String tailIds = query.getTailIds().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                conditions.add(String.format("tailId IN (%s)", tailIds));
            } else if (query.getTailNames() != null && !query.getTailNames().isEmpty()) {
                String tailNames = Arrays.stream(query.getTailNames().split(","))
                        .map(t -> "'" + t.trim().replace("'", "''") + "'")
                        .collect(Collectors.joining(","));
                conditions.add(String.format("tail IN (%s)", tailNames));
            }
        }

        // Full text search (simplified - using LIKE)
        if (query.hasFullTextSearch()) {
            String searchText = query.getFullTextSearch().replace("'", "''");
            conditions.add(String.format("message LIKE '%%%s%%'", searchText));
        }

        // Field conditions
        if (query.hasConditions()) {
            for (FieldCondition condition : query.getConditions()) {
                String conditionSql = buildConditionSql(condition);
                if (conditionSql != null && !conditionSql.isEmpty()) {
                    conditions.add(conditionSql);
                }
            }
        }

        return String.join(" AND ", conditions);
    }

    private String buildConditionSql(FieldCondition condition) {
        String field = condition.getField();
        Object value = condition.getValue();
        OperatorEnum operator = condition.getOperator();

        return switch (operator) {
            case EQ -> String.format("%s = '%s'", field, escapeValue(value));
            case NE -> String.format("%s != '%s'", field, escapeValue(value));
            case GT -> String.format("%s > '%s'", field, escapeValue(value));
            case GTE -> String.format("%s >= '%s'", field, escapeValue(value));
            case LT -> String.format("%s < '%s'", field, escapeValue(value));
            case LTE -> String.format("%s <= '%s'", field, escapeValue(value));
            case IN -> {
                String values = condition.getValues().stream()
                        .map(v -> "'" + escapeValue(v) + "'")
                        .collect(Collectors.joining(","));
                yield String.format("%s IN (%s)", field, values);
            }
            case LIKE -> String.format("%s LIKE '%%%s%%'", field, escapeValue(value));
            case REGEX -> String.format("%s REGEXP '%s'", field, escapeValue(value));
            default -> null;
        };
    }

    private String escapeValue(Object value) {
        if (value == null) return "";
        return value.toString().replace("'", "''");
    }

    private String buildStatisticsSql(String baseSql) {
        return "SELECT DATE_FORMAT(FROM_UNIXTIME(`timestamp` / 1000), '%Y-%m-%d %H:%i:%s') AS time_bucket, " +
                "COUNT(*) AS data_count " +
                "FROM (" + baseSql + ") data " +
                "GROUP BY time_bucket " +
                "ORDER BY time_bucket";
    }

    private List<Map<String, Object>> executeQuery(DataSource dataSource, String sql) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }

    private List<Long> executeStatQuery(DataSource dataSource, String sql, List<String> timestamps) throws SQLException {
        List<Long> counts = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                timestamps.add(rs.getString("time_bucket"));
                counts.add(rs.getLong("data_count"));
            }
        }
        return counts;
    }

    private List<LogDataItem> transformResults(List<Map<String, Object>> results, List<String> keyList) {
        List<LogDataItem> logDataList = new ArrayList<>();

        for (Map<String, Object> row : results) {
            LogDataItem item = new LogDataItem();

            // Set timestamp first
            item.putValue(LogParser.esKeyMap_timestamp, row.get(LogParser.esKeyMap_timestamp));

            // Set other fields
            for (String key : row.keySet()) {
                if (!HIDDEN_FIELDS.contains(key)) {
                    item.putValue(key, row.get(key));
                }
            }

            // Set standard fields
            item.setIp(getStringValue(row, LogParser.esKeyMap_logip));
            item.setFileName(getStringValue(row, LogParser.esKyeMap_fileName));
            item.setLineNumber(getStringValue(row, LogParser.esKeyMap_lineNumber));
            item.setTimestamp(getStringValue(row, LogParser.esKeyMap_timestamp));
            item.setJsonString(JSON.toJSONString(item.getData()));

            logDataList.add(item);
        }
        return logDataList;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : "";
    }
}
