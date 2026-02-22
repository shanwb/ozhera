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
package org.apache.ozhera.log.query.core.parser;

import org.apache.ozhera.log.query.api.dto.UnifiedLogQuery;

/**
 * Query parser interface for converting different query formats to UnifiedLogQuery
 */
public interface QueryParser {

    /**
     * Parse query string to UnifiedLogQuery
     *
     * @param queryString query string (SQL or other format)
     * @return UnifiedLogQuery
     * @throws QueryParseException if parsing fails
     */
    UnifiedLogQuery parse(String queryString) throws QueryParseException;

    /**
     * Check if this parser supports the given query format
     *
     * @param queryString query string
     * @return true if supported
     */
    boolean supports(String queryString);

    /**
     * Get parser type name
     *
     * @return parser type
     */
    String getParserType();
}
