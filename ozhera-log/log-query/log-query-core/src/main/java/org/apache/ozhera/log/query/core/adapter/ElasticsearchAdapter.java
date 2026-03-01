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
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import com.xiaomi.youpin.docean.plugin.es.antlr4.common.util.EsQueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.parse.LogParser;
import org.apache.ozhera.log.query.api.dto.*;
import org.apache.ozhera.log.query.api.enums.FullTextModeEnum;
import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;
import org.apache.ozhera.log.query.api.enums.OperatorEnum;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

/**
 * Elasticsearch storage adapter implementation
 */
@Slf4j
@AutoService(StorageAdapter.class)
public class ElasticsearchAdapter implements StorageAdapter {

    private static final Set<String> NO_HIGHLIGHT_FIELDS = Set.of("logstore", "tail");
    private static final Set<String> HIDDEN_FIELDS = Set.of("mqtag", "mqtopic", "logstore", "linenumber", "filename");
    private static final long QUERY_TIMEOUT_MINUTES = 2L;

    private final Map<Long, EsService> esServiceCache = new HashMap<>();

    @Override
    public LogStorageTypeEnum getStorageType() {
        return LogStorageTypeEnum.ELASTICSEARCH;
    }

    @Override
    public UnifiedLogResult query(UnifiedLogQuery query, StorageInfo storageInfo, List<String> keyList) {
        long startTime = System.currentTimeMillis();
        try {
            EsService esService = getEsService(storageInfo);
            if (esService == null) {
                log.error("EsService not found for clusterId: {}", storageInfo.getClusterId());
                return UnifiedLogResult.empty();
            }

            // Build query
            BoolQueryBuilder boolQuery = buildBoolQuery(query);
            ConstantScoreQueryBuilder constantScoreQuery = QueryBuilders.constantScoreQuery(boolQuery);

            // Build search source
            SearchSourceBuilder builder = buildSearchSourceBuilder(query, keyList, constantScoreQuery);

            // Execute search
            SearchRequest searchRequest = new SearchRequest(new String[]{storageInfo.getIndexName()}, builder);
            SearchResponse response = esService.search(searchRequest);

            // Transform response
            UnifiedLogResult result = transformSearchResponse(response, keyList, query.getEnableHighlight());
            result.setPage(query.getPage());
            result.setPageSize(query.getPageSize());
            result.setQueryTimeMs(System.currentTimeMillis() - startTime);
            result.setStorageType(LogStorageTypeEnum.ELASTICSEARCH.getCode());
            result.setDebugInfo(builder.toString());

            return result;
        } catch (Exception e) {
            log.error("ES query error, query: {}", query, e);
            return UnifiedLogResult.empty();
        }
    }

    @Override
    public UnifiedLogResult queryContext(LogContextQuery contextQuery, StorageInfo storageInfo, List<String> keyList) {
        try {
            EsService esService = getEsService(storageInfo);
            if (esService == null) {
                return UnifiedLogResult.empty();
            }

            List<LogDataItem> logDataList = new ArrayList<>();
            int times = 1;
            int pageSize = contextQuery.getPageSize();
            Long lineNumberSearchAfter = contextQuery.getLineNumber();
            List<Integer> logOrder = new ArrayList<>();
            logOrder.add(contextQuery.getType());

            if (contextQuery.getType() == 0) {
                times = 2;
                pageSize = pageSize / 2;
                logOrder.clear();
                logOrder.add(2); // before
                logOrder.add(1); // after
            }

            for (int t = 0; t < times; t++) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.filter(QueryBuilders.termQuery(LogParser.esKeyMap_logip, contextQuery.getIp()));
                boolQuery.filter(QueryBuilders.termQuery(LogParser.esKyeMap_fileName, contextQuery.getFileName()));

                SearchSourceBuilder builder = new SearchSourceBuilder();
                builder.query(boolQuery);

                if (logOrder.get(t) == 1) {
                    // after
                    builder.sort(LogParser.esKeyMap_timestamp, ASC);
                    builder.sort(LogParser.esKeyMap_lineNumber, ASC);
                } else {
                    // before
                    builder.sort(LogParser.esKeyMap_timestamp, DESC);
                    builder.sort(LogParser.esKeyMap_lineNumber, DESC);
                }

                if (contextQuery.getType() == 0 && logOrder.get(t) == 2) {
                    builder.searchAfter(new Object[]{contextQuery.getTimestamp(), lineNumberSearchAfter + 1});
                } else {
                    builder.searchAfter(new Object[]{contextQuery.getTimestamp(), lineNumberSearchAfter});
                }
                builder.size(pageSize);

                SearchRequest searchRequest = new SearchRequest(storageInfo.getIndexName());
                searchRequest.source(builder);
                SearchResponse response = esService.search(searchRequest);

                SearchHit[] hits = response.getHits().getHits();
                if (hits == null || hits.length == 0) {
                    continue;
                }

                if (logOrder.get(t) == 1) {
                    for (SearchHit hit : hits) {
                        logDataList.add(hitToLogDataItem(hit, keyList, false));
                    }
                } else {
                    for (int i = hits.length - 1; i >= 0; i--) {
                        logDataList.add(hitToLogDataItem(hits[i], keyList, false));
                    }
                }
            }

            return UnifiedLogResult.builder()
                    .logs(logDataList)
                    .total((long) logDataList.size())
                    .storageType(LogStorageTypeEnum.ELASTICSEARCH.getCode())
                    .build();
        } catch (Exception e) {
            log.error("ES context query error, contextQuery: {}", contextQuery, e);
            return UnifiedLogResult.empty();
        }
    }

    @Override
    public AggregationResult aggregate(UnifiedLogQuery query, StorageInfo storageInfo) {
        long startTime = System.currentTimeMillis();
        try {
            EsService esService = getEsService(storageInfo);
            if (esService == null || query.getAggregation() == null) {
                return new AggregationResult();
            }

            BoolQueryBuilder boolQuery = buildBoolQuery(query);
            AggregationSpec aggSpec = query.getAggregation();

            String interval = calculateHistogramInterval(query.getEndTimeMs() - query.getStartTimeMs());
            if (interval.isEmpty()) {
                return new AggregationResult();
            }

            EsClient.EsRet esRet = esService.dateHistogram(
                    storageInfo.getIndexName(),
                    aggSpec.getField() != null ? aggSpec.getField() : "timestamp",
                    interval,
                    query.getStartTimeMs(),
                    query.getEndTimeMs(),
                    boolQuery
            );

            AggregationResult result = AggregationResult.builder()
                    .timestamps(esRet.getTimestamps())
                    .counts(esRet.getCounts())
                    .queryTimeMs(System.currentTimeMillis() - startTime)
                    .debugInfo(boolQuery.toString())
                    .build();
            result.calculateTotalCount();

            return result;
        } catch (Exception e) {
            log.error("ES aggregation error, query: {}", query, e);
            return new AggregationResult();
        }
    }

    @Override
    public long count(UnifiedLogQuery query, StorageInfo storageInfo) {
        try {
            EsService esService = getEsService(storageInfo);
            if (esService == null) {
                return 0L;
            }

            BoolQueryBuilder boolQuery = buildBoolQuery(query);
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.query(boolQuery);
            builder.size(0);
            builder.trackTotalHits(true);

            SearchRequest searchRequest = new SearchRequest(new String[]{storageInfo.getIndexName()}, builder);
            SearchResponse response = esService.search(searchRequest);

            return response.getHits().getTotalHits().value;
        } catch (Exception e) {
            log.error("ES count error, query: {}", query, e);
            return 0L;
        }
    }

    @Override
    public boolean supportsHighlight() {
        return true;
    }

    @Override
    public boolean supportsSearchAfter() {
        return true;
    }

    @Override
    public boolean supportsFullTextSearch() {
        return true;
    }

    @Override
    public boolean supportsAggregation() {
        return true;
    }

    // ==================== Private Helper Methods ====================

    private EsService getEsService(StorageInfo storageInfo) {
        // In real implementation, this should get EsService from IoC container or cache
        return esServiceCache.get(storageInfo.getClusterId());
    }

    public void registerEsService(Long clusterId, EsService esService) {
        esServiceCache.put(clusterId, esService);
    }

    private BoolQueryBuilder buildBoolQuery(UnifiedLogQuery query) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Time range filter
        boolQuery.filter(QueryBuilders.rangeQuery("timestamp")
                .from(query.getStartTimeMs())
                .to(query.getEndTimeMs()));

        // Tail filter
        if (query.hasTailFilter()) {
            BoolQueryBuilder tailQuery = QueryBuilders.boolQuery();
            if (query.getTailIds() != null && !query.getTailIds().isEmpty()) {
                for (Long tailId : query.getTailIds()) {
                    tailQuery.should(QueryBuilders.termQuery("tailId", tailId));
                }
            } else if (query.getTailNames() != null && !query.getTailNames().isEmpty()) {
                String[] tailNames = query.getTailNames().split(",");
                for (String tailName : tailNames) {
                    tailQuery.should(QueryBuilders.termQuery("tail", tailName.trim()));
                }
            }
            tailQuery.minimumShouldMatch(1);
            boolQuery.filter(tailQuery);
        }

        // Full text search
        if (query.hasFullTextSearch()) {
            QueryBuilder fullTextQuery = buildFullTextQuery(query.getFullTextSearch(), query.getFullTextMode());
            if (fullTextQuery != null) {
                boolQuery.filter(fullTextQuery);
            }
        }

        // Field conditions
        if (query.hasConditions()) {
            for (FieldCondition condition : query.getConditions()) {
                QueryBuilder conditionQuery = buildConditionQuery(condition);
                if (conditionQuery != null) {
                    if (condition.getLogic() == FieldCondition.LogicEnum.OR) {
                        boolQuery.should(conditionQuery);
                    } else {
                        boolQuery.filter(conditionQuery);
                    }
                }
            }
        }

        return boolQuery;
    }

    private QueryBuilder buildFullTextQuery(String fullTextSearch, FullTextModeEnum mode) {
        // Use antlr4 parser for complex query syntax
        SearchSourceBuilder searchSourceBuilder = EsQueryUtils.getSearchSourceBuilder(fullTextSearch);
        if (searchSourceBuilder != null) {
            return searchSourceBuilder.query();
        }
        return null;
    }

    private QueryBuilder buildConditionQuery(FieldCondition condition) {
        String field = condition.getField();
        Object value = condition.getValue();
        OperatorEnum operator = condition.getOperator();

        return switch (operator) {
            case EQ -> QueryBuilders.termQuery(field, value);
            case NE -> QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(field, value));
            case GT -> QueryBuilders.rangeQuery(field).gt(value);
            case GTE -> QueryBuilders.rangeQuery(field).gte(value);
            case LT -> QueryBuilders.rangeQuery(field).lt(value);
            case LTE -> QueryBuilders.rangeQuery(field).lte(value);
            case IN -> QueryBuilders.termsQuery(field, condition.getValues());
            case NOT_IN -> QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(field, condition.getValues()));
            case LIKE -> QueryBuilders.wildcardQuery(field, value.toString());
            case REGEX -> QueryBuilders.regexpQuery(field, value.toString());
            case EXISTS -> QueryBuilders.existsQuery(field);
            case NOT_EXISTS -> QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field));
            default -> null;
        };
    }

    private SearchSourceBuilder buildSearchSourceBuilder(UnifiedLogQuery query, List<String> keyList,
                                                          ConstantScoreQueryBuilder queryBuilder) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(queryBuilder);
        builder.trackScores(false);

        // Sorting
        SortOrder sortOrder = query.getAscending() ? ASC : DESC;
        builder.sort(query.getSortField(), sortOrder);

        // Pagination
        if (query.getSearchAfter() != null && query.getIsExport()) {
            builder.from(0);
            builder.size(query.getPageSize());
            builder.searchAfter(query.getSearchAfter());
        } else {
            builder.from(query.getOffset());
            builder.size(query.getPageSize());
        }

        // Highlight (only when not exporting)
        if (query.getEnableHighlight() && !query.getIsExport()) {
            builder.highlighter(buildHighlightBuilder(keyList, query.getHighlightFields()));
        }

        builder.timeout(TimeValue.timeValueMinutes(QUERY_TIMEOUT_MINUTES));
        return builder;
    }

    private HighlightBuilder buildHighlightBuilder(List<String> keyList, List<String> highlightFields) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        List<String> fieldsToHighlight = highlightFields != null && !highlightFields.isEmpty()
                ? highlightFields : keyList;

        for (String key : fieldsToHighlight) {
            if (!NO_HIGHLIGHT_FIELDS.contains(key)) {
                highlightBuilder.field(new HighlightBuilder.Field(key));
            }
        }
        return highlightBuilder;
    }

    private UnifiedLogResult transformSearchResponse(SearchResponse response, List<String> keyList,
                                                      boolean enableHighlight) {
        SearchHit[] hits = response.getHits().getHits();
        if (hits == null || hits.length == 0) {
            return UnifiedLogResult.builder()
                    .logs(Collections.emptyList())
                    .total(response.getHits().getTotalHits().value)
                    .hasMore(false)
                    .build();
        }

        List<LogDataItem> logDataList = new ArrayList<>();
        for (SearchHit hit : hits) {
            LogDataItem item = hitToLogDataItem(hit, keyList, enableHighlight);
            logDataList.add(item);
        }

        Object[] lastSortValues = hits[hits.length - 1].getSortValues();

        return UnifiedLogResult.builder()
                .logs(logDataList)
                .total(response.getHits().getTotalHits().value)
                .lastSortValues(lastSortValues)
                .hasMore(logDataList.size() >= response.getHits().getTotalHits().value ? false : true)
                .build();
    }

    private LogDataItem hitToLogDataItem(SearchHit hit, List<String> keyList, boolean enableHighlight) {
        Map<String, Object> sourceMap = hit.getSourceAsMap();
        LogDataItem item = new LogDataItem();

        // Set timestamp first
        if (sourceMap.containsKey(LogParser.esKeyMap_timestamp)) {
            item.putValue(LogParser.esKeyMap_timestamp, sourceMap.get(LogParser.esKeyMap_timestamp));
        }

        // Set other fields
        for (String key : keyList) {
            if (!HIDDEN_FIELDS.contains(key) && sourceMap.containsKey(key)) {
                item.putValue(key, sourceMap.get(key));
            }
        }

        // Set standard fields
        item.setIp(getStringValue(sourceMap, LogParser.esKeyMap_logip));
        item.setFileName(getStringValue(sourceMap, LogParser.esKyeMap_fileName));
        item.setLineNumber(getStringValue(sourceMap, LogParser.esKeyMap_lineNumber));
        item.setTimestamp(getStringValue(sourceMap, LogParser.esKeyMap_timestamp));
        item.setJsonString(JSON.toJSONString(item.getData()));
        item.setSortValues(hit.getSortValues());

        // Set highlight
        if (enableHighlight) {
            item.setHighlight(extractHighlight(hit));
        }

        return item;
    }

    private Map<String, List<String>> extractHighlight(SearchHit hit) {
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        if (highlightFields == null || highlightFields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> highlightMap = new HashMap<>();
        for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
            List<String> fragments = Arrays.stream(entry.getValue().getFragments())
                    .map(Object::toString)
                    .collect(Collectors.toList());
            highlightMap.put(entry.getKey(), fragments);
        }
        return highlightMap;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : "";
    }

    private String calculateHistogramInterval(Long durationMs) {
        long duration = durationMs / 1000;
        if (duration > 24 * 60 * 60) {
            return (duration / 100) + "s";
        } else if (duration > 12 * 60 * 60) {
            return (duration / 80) + "s";
        } else if (duration > 6 * 60 * 60) {
            return (duration / 60) + "s";
        } else if (duration > 60 * 60) {
            return (duration / 50) + "s";
        } else if (duration > 30 * 60) {
            return (duration / 40) + "s";
        } else if (duration > 10 * 60) {
            return (duration / 30) + "s";
        } else if (duration > 5 * 60) {
            return (duration / 25) + "s";
        } else if (duration > 3 * 60) {
            return (duration / 20) + "s";
        } else if (duration > 60) {
            return (duration / 15) + "s";
        } else if (duration > 10) {
            return (duration / 10) + "s";
        } else {
            return "";
        }
    }
}
