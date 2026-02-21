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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single log data item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDataItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log data as key-value pairs
     */
    @Builder.Default
    private Map<String, Object> data = new LinkedHashMap<>();

    /**
     * Log data as JSON string
     */
    private String jsonString;

    /**
     * Log file name
     */
    private String fileName;

    /**
     * Line number in the log file
     */
    private String lineNumber;

    /**
     * IP address of the log source
     */
    private String ip;

    /**
     * Timestamp of the log
     */
    private String timestamp;

    /**
     * Highlight fields and their highlighted fragments
     */
    private Map<String, List<String>> highlight;

    /**
     * Sort values for pagination (used in searchAfter)
     */
    private Object[] sortValues;

    public void putValue(String key, Object value) {
        if (data == null) {
            data = new LinkedHashMap<>();
        }
        data.put(key, value);
    }

    public Object getValue(String key) {
        return data != null ? data.get(key) : null;
    }
}
