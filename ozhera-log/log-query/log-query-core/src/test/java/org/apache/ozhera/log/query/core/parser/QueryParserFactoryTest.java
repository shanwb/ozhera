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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for QueryParserFactory
 */
public class QueryParserFactoryTest {

    @Test
    public void testGetSqlParser() {
        QueryParser parser = QueryParserFactory.getSqlParser();
        assertNotNull(parser);
        assertEquals("SQL", parser.getParserType());
    }

    @Test
    public void testGetParserByType() {
        QueryParser parser = QueryParserFactory.getParserByType("SQL");
        assertNotNull(parser);
        assertTrue(parser instanceof SqlQueryParser);
    }

    @Test
    public void testGetParserByTypeCaseInsensitive() {
        QueryParser parser1 = QueryParserFactory.getParserByType("sql");
        QueryParser parser2 = QueryParserFactory.getParserByType("SQL");
        QueryParser parser3 = QueryParserFactory.getParserByType("Sql");

        assertNotNull(parser1);
        assertNotNull(parser2);
        assertNotNull(parser3);
    }

    @Test
    public void testGetParserForSelectQuery() {
        QueryParser parser = QueryParserFactory.getParser("SELECT * FROM logs");
        assertNotNull(parser);
        assertEquals("SQL", parser.getParserType());
    }

    @Test
    public void testGetParserForUnsupportedQuery() {
        QueryParser parser = QueryParserFactory.getParser("some random text");
        assertNull(parser);
    }

    @Test
    public void testGetAllParsers() {
        var parsers = QueryParserFactory.getAllParsers();
        assertNotNull(parsers);
        assertFalse(parsers.isEmpty());

        boolean hasSqlParser = parsers.stream()
                .anyMatch(p -> "SQL".equals(p.getParserType()));
        assertTrue(hasSqlParser);
    }

    @Test
    public void testGetParserForNull() {
        QueryParser parser = QueryParserFactory.getParser(null);
        assertNull(parser);
    }

    @Test
    public void testGetParserForEmpty() {
        QueryParser parser = QueryParserFactory.getParser("");
        assertNull(parser);
    }
}
