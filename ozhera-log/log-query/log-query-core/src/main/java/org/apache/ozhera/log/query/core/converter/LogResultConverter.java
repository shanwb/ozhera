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

import org.apache.ozhera.log.query.api.dto.LogDataItem;
import org.apache.ozhera.log.query.api.dto.UnifiedLogResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converter for UnifiedLogResult to legacy format
 * This helps maintain backward compatibility with existing clients
 */
public class LogResultConverter {

    /**
     * Convert UnifiedLogResult to legacy LogDTO format (as Map)
     *
     * @param result unified log result
     * @return legacy format map
     */
    public static Map<String, Object> toLegacyFormat(UnifiedLogResult result) {
        Map<String, Object> legacyResult = new LinkedHashMap<>();

        if (result == null) {
            legacyResult.put("logDataDTOList", List.of());
            legacyResult.put("total", 0L);
            return legacyResult;
        }

        List<Map<String, Object>> logDataDTOList = result.getLogs().stream()
                .map(LogResultConverter::convertLogDataItem)
                .collect(Collectors.toList());

        legacyResult.put("logDataDTOList", logDataDTOList);
        legacyResult.put("total", result.getTotal());
        legacyResult.put("thisSortValue", result.getLastSortValues());

        return legacyResult;
    }

    /**
     * Convert LogDataItem to legacy LogDataDTO format (as Map)
     */
    private static Map<String, Object> convertLogDataItem(LogDataItem item) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("logOfKV", item.getData());
        dto.put("logOfString", item.getJsonString());
        dto.put("fileName", item.getFileName());
        dto.put("lineNumber", item.getLineNumber());
        dto.put("ip", item.getIp());
        dto.put("timestamp", item.getTimestamp());
        dto.put("highlight", item.getHighlight());

        return dto;
    }

    /**
     * Convert UnifiedLogResult to simple list format for export
     *
     * @param result unified log result
     * @return list of log data maps
     */
    public static List<Map<String, Object>> toExportFormat(UnifiedLogResult result) {
        if (result == null || result.getLogs() == null) {
            return List.of();
        }

        return result.getLogs().stream()
                .map(item -> {
                    Map<String, Object> row = new LinkedHashMap<>(item.getData());
                    row.put("ip", item.getIp());
                    row.put("fileName", item.getFileName());
                    row.put("lineNumber", item.getLineNumber());
                    return row;
                })
                .collect(Collectors.toList());
    }
}
