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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Factory for query parsers
 */
public class QueryParserFactory {

    private static final List<QueryParser> PARSERS = new ArrayList<>();

    static {
        // Register default parsers
        PARSERS.add(new SqlQueryParser());

        // Load additional parsers via SPI
        ServiceLoader<QueryParser> loader = ServiceLoader.load(QueryParser.class);
        for (QueryParser parser : loader) {
            if (!containsParserType(parser.getParserType())) {
                PARSERS.add(parser);
            }
        }
    }

    private static boolean containsParserType(String parserType) {
        return PARSERS.stream().anyMatch(p -> p.getParserType().equals(parserType));
    }

    /**
     * Get parser that supports the given query string
     *
     * @param queryString query string
     * @return matching parser or null
     */
    public static QueryParser getParser(String queryString) {
        for (QueryParser parser : PARSERS) {
            if (parser.supports(queryString)) {
                return parser;
            }
        }
        return null;
    }

    /**
     * Get parser by type
     *
     * @param parserType parser type name
     * @return parser or null
     */
    public static QueryParser getParserByType(String parserType) {
        for (QueryParser parser : PARSERS) {
            if (parser.getParserType().equalsIgnoreCase(parserType)) {
                return parser;
            }
        }
        return null;
    }

    /**
     * Get SQL parser
     *
     * @return SQL parser
     */
    public static QueryParser getSqlParser() {
        return getParserByType("SQL");
    }

    /**
     * Register a custom parser
     *
     * @param parser parser to register
     */
    public static void registerParser(QueryParser parser) {
        if (!containsParserType(parser.getParserType())) {
            PARSERS.add(parser);
        }
    }

    /**
     * Get all registered parsers
     *
     * @return list of parsers
     */
    public static List<QueryParser> getAllParsers() {
        return new ArrayList<>(PARSERS);
    }
}
