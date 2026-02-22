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

import org.apache.ozhera.log.query.api.dto.FieldCondition;
import org.apache.ozhera.log.query.api.dto.UnifiedLogQuery;
import org.apache.ozhera.log.query.api.enums.OperatorEnum;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SqlQueryParser
 */
public class SqlQueryParserTest {

    private SqlQueryParser parser;

    @Before
    public void setUp() {
        parser = new SqlQueryParser();
    }

    @Test
    public void testSupports() {
        assertTrue(parser.supports("SELECT * FROM logs"));
        assertTrue(parser.supports("select * from logs"));
        assertTrue(parser.supports("  SELECT * FROM logs  "));
        assertFalse(parser.supports("INSERT INTO logs VALUES (1)"));
        assertFalse(parser.supports("UPDATE logs SET a=1"));
        assertFalse(parser.supports(null));
        assertFalse(parser.supports(""));
    }

    @Test
    public void testParseSimpleSelect() {
        String sql = "SELECT * FROM logs";
        UnifiedLogQuery query = parser.parse(sql);
        assertNotNull(query);
    }

    @Test
    public void testParseWithTimeRange() {
        String sql = "SELECT * FROM logs WHERE timestamp >= 1000 AND timestamp <= 2000";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Long.valueOf(1000), query.getStartTime());
        assertEquals(Long.valueOf(2000), query.getEndTime());
    }

    @Test
    public void testParseWithTimestampBetween() {
        String sql = "SELECT * FROM logs WHERE timestamp BETWEEN 1000 AND 2000";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Long.valueOf(1000), query.getStartTime());
        assertEquals(Long.valueOf(2000), query.getEndTime());
    }

    @Test
    public void testParseWithStoreId() {
        String sql = "SELECT * FROM logs WHERE storeId = 123";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Long.valueOf(123), query.getStoreId());
    }

    @Test
    public void testParseWithTail() {
        String sql = "SELECT * FROM logs WHERE tail = 'app-log'";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals("app-log", query.getTailNames());
    }

    @Test
    public void testParseWithFullTextSearch() {
        String sql = "SELECT * FROM logs WHERE message LIKE '%error%'";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals("error", query.getFullTextSearch());
    }

    @Test
    public void testParseWithFieldCondition() {
        String sql = "SELECT * FROM logs WHERE level = 'ERROR'";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertNotNull(query.getConditions());
        assertEquals(1, query.getConditions().size());

        FieldCondition condition = query.getConditions().get(0);
        assertEquals("level", condition.getField());
        assertEquals(OperatorEnum.EQ, condition.getOperator());
        assertEquals("ERROR", condition.getValue());
    }

    @Test
    public void testParseWithMultipleConditions() {
        String sql = "SELECT * FROM logs WHERE level = 'ERROR' AND host = 'server1'";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertNotNull(query.getConditions());
        assertEquals(2, query.getConditions().size());
    }

    @Test
    public void testParseWithInClause() {
        String sql = "SELECT * FROM logs WHERE level IN ('ERROR', 'WARN')";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertNotNull(query.getConditions());
        assertEquals(1, query.getConditions().size());

        FieldCondition condition = query.getConditions().get(0);
        assertEquals("level", condition.getField());
        assertEquals(OperatorEnum.IN, condition.getOperator());
        assertNotNull(condition.getValues());
        assertEquals(2, condition.getValues().size());
    }

    @Test
    public void testParseWithOrderBy() {
        String sql = "SELECT * FROM logs ORDER BY timestamp DESC";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals("timestamp", query.getSortField());
        assertFalse(query.getAscending());
    }

    @Test
    public void testParseWithOrderByAsc() {
        String sql = "SELECT * FROM logs ORDER BY timestamp ASC";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals("timestamp", query.getSortField());
        assertTrue(query.getAscending());
    }

    @Test
    public void testParseWithLimit() {
        String sql = "SELECT * FROM logs LIMIT 50";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Integer.valueOf(50), query.getPageSize());
    }

    @Test
    public void testParseWithLimitOffset() {
        String sql = "SELECT * FROM logs LIMIT 100, 50";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Integer.valueOf(50), query.getPageSize());
    }

    @Test
    public void testParseComplexQuery() {
        String sql = "SELECT * FROM logs " +
                "WHERE timestamp >= 1000 AND timestamp <= 2000 " +
                "AND storeId = 123 " +
                "AND level = 'ERROR' " +
                "AND message LIKE '%exception%' " +
                "ORDER BY timestamp DESC " +
                "LIMIT 100";

        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertEquals(Long.valueOf(1000), query.getStartTime());
        assertEquals(Long.valueOf(2000), query.getEndTime());
        assertEquals(Long.valueOf(123), query.getStoreId());
        assertEquals("exception", query.getFullTextSearch());
        assertEquals("timestamp", query.getSortField());
        assertFalse(query.getAscending());
        assertEquals(Integer.valueOf(100), query.getPageSize());
    }

    @Test(expected = QueryParseException.class)
    public void testParseInvalidSql() {
        parser.parse("INVALID SQL STATEMENT");
    }

    @Test(expected = QueryParseException.class)
    public void testParseEmptyString() {
        parser.parse("");
    }

    @Test(expected = QueryParseException.class)
    public void testParseNull() {
        parser.parse(null);
    }

    @Test(expected = QueryParseException.class)
    public void testParseNonSelectStatement() {
        parser.parse("INSERT INTO logs VALUES (1, 'test')");
    }

    @Test
    public void testParseWithComparisonOperators() {
        String sql = "SELECT * FROM logs WHERE count > 10 AND count <= 100";
        UnifiedLogQuery query = parser.parse(sql);

        assertNotNull(query);
        assertNotNull(query.getConditions());
        assertEquals(2, query.getConditions().size());

        FieldCondition gt = query.getConditions().get(0);
        assertEquals("count", gt.getField());
        assertEquals(OperatorEnum.GT, gt.getOperator());

        FieldCondition lte = query.getConditions().get(1);
        assertEquals("count", lte.getField());
        assertEquals(OperatorEnum.LTE, lte.getOperator());
    }
}
