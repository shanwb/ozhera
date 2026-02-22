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

/**
 * Exception thrown when query parsing fails
 */
public class QueryParseException extends RuntimeException {

    private final String queryString;
    private final String parserType;

    public QueryParseException(String message) {
        super(message);
        this.queryString = null;
        this.parserType = null;
    }

    public QueryParseException(String message, Throwable cause) {
        super(message, cause);
        this.queryString = null;
        this.parserType = null;
    }

    public QueryParseException(String message, String queryString, String parserType) {
        super(message);
        this.queryString = queryString;
        this.parserType = parserType;
    }

    public QueryParseException(String message, String queryString, String parserType, Throwable cause) {
        super(message, cause);
        this.queryString = queryString;
        this.parserType = parserType;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParserType() {
        return parserType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (parserType != null) {
            sb.append(" [parser=").append(parserType).append("]");
        }
        if (queryString != null) {
            sb.append(" [query=").append(queryString.length() > 100
                    ? queryString.substring(0, 100) + "..." : queryString).append("]");
        }
        return sb.toString();
    }
}
