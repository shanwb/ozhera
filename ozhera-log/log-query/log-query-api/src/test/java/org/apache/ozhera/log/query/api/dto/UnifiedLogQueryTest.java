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
        assertEquals(Long.valueOf(1000), query.getStartTime());
        assertEquals(Long.valueOf(2000), query.getEndTime());
        assertEquals("error", query.getFullTextSearch());
        assertEquals(2, query.getConditions().size());
    }
}
