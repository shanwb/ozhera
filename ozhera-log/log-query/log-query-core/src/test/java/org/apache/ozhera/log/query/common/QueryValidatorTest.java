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
package org.apache.ozhera.log.query.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for QueryValidator
 */
public class QueryValidatorTest {

    @Test
    public void testValidateTimeRange_Normal() {
        long[] range = QueryValidator.validateTimeRange(1000L, 2000L);
        assertEquals(1000L, range[0]);
        assertEquals(2000L, range[1]);
    }

    @Test
    public void testValidateTimeRange_Reversed() {
        long[] range = QueryValidator.validateTimeRange(2000L, 1000L);
        assertEquals(1000L, range[0]);
        assertEquals(2000L, range[1]);
    }

    @Test
    public void testValidateTimeRange_NullStart() {
        long now = System.currentTimeMillis();
        long[] range = QueryValidator.validateTimeRange(null, now);
        assertTrue(range[0] < now);
        assertEquals(now, range[1]);
    }

    @Test
    public void testValidateTimeRange_NullEnd() {
        long start = System.currentTimeMillis() - 1000;
        long[] range = QueryValidator.validateTimeRange(start, null);
        assertEquals(start, range[0]);
        assertTrue(range[1] >= start);
    }

    @Test
    public void testValidateTimeRange_ExceedsMaxRange() {
        long end = System.currentTimeMillis();
        long start = end - (10 * 24 * 60 * 60 * 1000L); // 10 days ago
        long[] range = QueryValidator.validateTimeRange(start, end);

        // Should be limited to 7 days
        long maxRange = 7 * 24 * 60 * 60 * 1000L;
        assertTrue(range[1] - range[0] <= maxRange);
    }

    @Test
    public void testValidatePageSize_Normal() {
        assertEquals(50, QueryValidator.validatePageSize(50));
    }

    @Test
    public void testValidatePageSize_Null() {
        assertEquals(100, QueryValidator.validatePageSize(null));
    }

    @Test
    public void testValidatePageSize_Zero() {
        assertEquals(100, QueryValidator.validatePageSize(0));
    }

    @Test
    public void testValidatePageSize_Negative() {
        assertEquals(100, QueryValidator.validatePageSize(-10));
    }

    @Test
    public void testValidatePageSize_ExceedsMax() {
        assertEquals(10000, QueryValidator.validatePageSize(20000));
    }

    @Test
    public void testValidatePage_Normal() {
        assertEquals(5, QueryValidator.validatePage(5));
    }

    @Test
    public void testValidatePage_Null() {
        assertEquals(1, QueryValidator.validatePage(null));
    }

    @Test
    public void testValidatePage_Zero() {
        assertEquals(1, QueryValidator.validatePage(0));
    }

    @Test
    public void testValidatePage_Negative() {
        assertEquals(1, QueryValidator.validatePage(-1));
    }

    @Test
    public void testIsSearchTextSafe_Normal() {
        assertTrue(QueryValidator.isSearchTextSafe("error"));
        assertTrue(QueryValidator.isSearchTextSafe("NullPointerException"));
        assertTrue(QueryValidator.isSearchTextSafe("level:ERROR"));
    }

    @Test
    public void testIsSearchTextSafe_Null() {
        assertTrue(QueryValidator.isSearchTextSafe(null));
    }

    @Test
    public void testIsSearchTextSafe_Empty() {
        assertTrue(QueryValidator.isSearchTextSafe(""));
    }

    @Test
    public void testIsSearchTextSafe_SqlInjection() {
        assertFalse(QueryValidator.isSearchTextSafe("'; DROP TABLE logs; --"));
        assertFalse(QueryValidator.isSearchTextSafe("1; DELETE FROM logs"));
        assertFalse(QueryValidator.isSearchTextSafe("test /* comment */"));
    }

    @Test
    public void testSanitizeSearchText_Normal() {
        assertEquals("error", QueryValidator.sanitizeSearchText("error"));
    }

    @Test
    public void testSanitizeSearchText_WithQuotes() {
        assertEquals("test''value", QueryValidator.sanitizeSearchText("test'value"));
    }

    @Test
    public void testSanitizeSearchText_WithBackslash() {
        assertEquals("test\\\\value", QueryValidator.sanitizeSearchText("test\\value"));
    }

    @Test
    public void testSanitizeSearchText_WithSemicolon() {
        assertEquals("testvalue", QueryValidator.sanitizeSearchText("test;value"));
    }

    @Test
    public void testSanitizeSearchText_Null() {
        assertNull(QueryValidator.sanitizeSearchText(null));
    }

    @Test
    public void testSanitizeSearchText_Trim() {
        assertEquals("test", QueryValidator.sanitizeSearchText("  test  "));
    }
}
