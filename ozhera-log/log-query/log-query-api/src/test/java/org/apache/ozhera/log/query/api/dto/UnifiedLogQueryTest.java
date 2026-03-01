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

import org.apache.ozhera.log.query.api.enums.OperatorEnum;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for UnifiedLogQuery
 */
public class UnifiedLogQueryTest {

    @Test
    public void testBuilderDefaults() {
        UnifiedLogQuery query = UnifiedLogQuery.builder().build();

        assertEquals("timestamp", query.getSortField());
        assertFalse(query.getAscending());
        assertEquals(Integer.valueOf(1), query.getPage());
        assertEquals(Integer.valueOf(100), query.getPageSize());
        assertTrue(query.getEnableHighlight());
        assertFalse(query.getIsExport());
    }

    @Test
    public void testGetOffset() {
        UnifiedLogQuery query = UnifiedLogQuery.builder()
                .page(3)
                .pageSize(50)
                .build();

        assertEquals(100, query.getOffset());
    }

    @Test
    public void testHasFullTextSearch() {
        UnifiedLogQuery query1 = UnifiedLogQuery.builder().build();
        assertFalse(query1.hasFullTextSearch());

        UnifiedLogQuery query2 = UnifiedLogQuery.builder()
                .fullTextSearch("")
                .build();
        assertFalse(query2.hasFullTextSearch());

        UnifiedLogQuery query3 = UnifiedLogQuery.builder()
                .fullTextSearch("  ")
                .build();
        assertFalse(query3.hasFullTextSearch());

        UnifiedLogQuery query4 = UnifiedLogQuery.builder()
                .fullTextSearch("error")
                .build();
        assertTrue(query4.hasFullTextSearch());
    }

    @Test
    public void testHasConditions() {
        UnifiedLogQuery query1 = UnifiedLogQuery.builder().build();
        assertFalse(query1.hasConditions());

        UnifiedLogQuery query2 = UnifiedLogQuery.builder()
                .conditions(List.of())
                .build();
        assertFalse(query2.hasConditions());

        UnifiedLogQuery query3 = UnifiedLogQuery.builder()
                .conditions(List.of(FieldCondition.eq("level", "ERROR")))
                .build();
        assertTrue(query3.hasConditions());
    }

    @Test
    public void testHasTailFilter() {
        UnifiedLogQuery query1 = UnifiedLogQuery.builder().build();
        assertFalse(query1.hasTailFilter());

        UnifiedLogQuery query2 = UnifiedLogQuery.builder()
                .tailIds(List.of(1L, 2L))
                .build();
        assertTrue(query2.hasTailFilter());

        UnifiedLogQuery query3 = UnifiedLogQuery.builder()
                .tailNames("app-log,web-log")
                .build();
        assertTrue(query3.hasTailFilter());
    }

    @Test
    public void testFullBuilder() {
        UnifiedLogQuery query = UnifiedLogQuery.builder()
                .storeId(123L)
                .storeName("test-store")
                .tailIds(List.of(1L, 2L))
                .startTime(1000L)
                .endTime(2000L)
                .fullTextSearch("error")
                .conditions(List.of(
                        FieldCondition.eq("level", "ERROR"),
                        FieldCondition.in("host", List.of("server1", "server2"))
                ))
                .sortField("timestamp")
                .ascending(false)
                .page(1)
                .pageSize(100)
                .enableHighlight(true)
                .build();

        assertEquals(Long.valueOf(123), query.getStoreId());
        assertEquals("test-store", query.getStoreName());
        assertEquals(2, query.getTailIds().size());
        assertEquals(Long.valueOf(1000), query.getStartTimeMs());
        assertEquals(Long.valueOf(2000), query.getEndTimeMs());
        assertEquals("error", query.getFullTextSearch());
        assertEquals(2, query.getConditions().size());
    }

    @Test
    public void testTimeParsing_MillisecondTimestamp() {
        // Test with Long timestamp
        UnifiedLogQuery query1 = UnifiedLogQuery.builder()
                .startTime(1738368000000L)
                .endTime(1738454400000L)
                .build();
        assertEquals(Long.valueOf(1738368000000L), query1.getStartTimeMs());
        assertEquals(Long.valueOf(1738454400000L), query1.getEndTimeMs());

        // Test with String timestamp
        UnifiedLogQuery query2 = UnifiedLogQuery.builder()
                .startTime("1738368000000")
                .endTime("1738454400000")
                .build();
        assertEquals(Long.valueOf(1738368000000L), query2.getStartTimeMs());
        assertEquals(Long.valueOf(1738454400000L), query2.getEndTimeMs());
    }

    @Test
    public void testTimeParsing_Iso8601Format() {
        // Test with ISO 8601 format
        UnifiedLogQuery query = UnifiedLogQuery.builder()
                .startTime("2024-01-01T00:00:00Z")
                .endTime("2024-01-02T00:00:00Z")
                .build();
        assertEquals(Long.valueOf(1704067200000L), query.getStartTimeMs());
        assertEquals(Long.valueOf(1704153600000L), query.getEndTimeMs());
    }

    @Test
    public void testTimeParsing_DateFormat() {
        // Test with date only format
        UnifiedLogQuery query = UnifiedLogQuery.builder()
                .startTime("2024-01-01")
                .endTime("2024-01-02")
                .build();
        assertNotNull(query.getStartTimeMs());
        assertNotNull(query.getEndTimeMs());
        assertTrue(query.getEndTimeMs() > query.getStartTimeMs());
    }

    @Test
    public void testTimeParsing_DateTimeFormat() {
        // Test with date time format
        UnifiedLogQuery query = UnifiedLogQuery.builder()
                .startTime("2024-01-01 00:00:00")
                .endTime("2024-01-02 12:30:45")
                .build();
        assertNotNull(query.getStartTimeMs());
        assertNotNull(query.getEndTimeMs());
        assertTrue(query.getEndTimeMs() > query.getStartTimeMs());
    }

    @Test
    public void testIsValid() {
        // Valid query
        UnifiedLogQuery validQuery = UnifiedLogQuery.builder()
                .storeId(1L)
                .startTime(1738368000000L)
                .endTime(1738454400000L)
                .build();
        assertTrue(validQuery.isValid());

        // Missing storeId
        UnifiedLogQuery missingStoreId = UnifiedLogQuery.builder()
                .startTime(1738368000000L)
                .endTime(1738454400000L)
                .build();
        assertFalse(missingStoreId.isValid());

        // Invalid time range
        UnifiedLogQuery invalidTimeRange = UnifiedLogQuery.builder()
                .storeId(1L)
                .startTime(1738454400000L)
                .endTime(1738368000000L)
                .build();
        assertFalse(invalidTimeRange.isValid());

        // With string dates
        UnifiedLogQuery stringDates = UnifiedLogQuery.builder()
                .storeId(1L)
                .startTime("2024-01-01")
                .endTime("2024-01-02")
                .build();
        assertTrue(stringDates.isValid());
    }
}
