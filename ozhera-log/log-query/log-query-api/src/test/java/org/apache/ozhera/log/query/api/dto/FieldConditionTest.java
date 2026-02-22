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
 * Unit tests for FieldCondition
 */
public class FieldConditionTest {

    @Test
    public void testEqFactory() {
        FieldCondition condition = FieldCondition.eq("level", "ERROR");

        assertEquals("level", condition.getField());
        assertEquals(OperatorEnum.EQ, condition.getOperator());
        assertEquals("ERROR", condition.getValue());
        assertEquals(FieldCondition.LogicEnum.AND, condition.getLogic());
    }

    @Test
    public void testInFactory() {
        FieldCondition condition = FieldCondition.in("host", List.of("server1", "server2"));

        assertEquals("host", condition.getField());
        assertEquals(OperatorEnum.IN, condition.getOperator());
        assertNotNull(condition.getValues());
        assertEquals(2, condition.getValues().size());
    }

    @Test
    public void testLikeFactory() {
        FieldCondition condition = FieldCondition.like("message", "%error%");

        assertEquals("message", condition.getField());
        assertEquals(OperatorEnum.LIKE, condition.getOperator());
        assertEquals("%error%", condition.getValue());
    }

    @Test
    public void testBuilder() {
        FieldCondition condition = FieldCondition.builder()
                .field("count")
                .operator(OperatorEnum.GT)
                .value(100)
                .logic(FieldCondition.LogicEnum.OR)
                .build();

        assertEquals("count", condition.getField());
        assertEquals(OperatorEnum.GT, condition.getOperator());
        assertEquals(100, condition.getValue());
        assertEquals(FieldCondition.LogicEnum.OR, condition.getLogic());
    }

    @Test
    public void testDefaultLogic() {
        FieldCondition condition = FieldCondition.builder()
                .field("test")
                .operator(OperatorEnum.EQ)
                .value("value")
                .build();

        assertEquals(FieldCondition.LogicEnum.AND, condition.getLogic());
    }
}
