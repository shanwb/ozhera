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

/**
 * Log context query request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogContextQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Store ID
     */
    private Long storeId;

    /**
     * Store name
     */
    private String storeName;

    /**
     * IP address of the log source
     */
    private String ip;

    /**
     * Log file name
     */
    private String fileName;

    /**
     * Line number of the anchor log
     */
    private Long lineNumber;

    /**
     * Timestamp of the anchor log
     */
    private String timestamp;

    /**
     * Context type: 0-both, 1-after, 2-before
     */
    @Builder.Default
    private Integer type = 0;

    /**
     * Number of context lines to fetch
     */
    @Builder.Default
    private Integer pageSize = 20;
}
