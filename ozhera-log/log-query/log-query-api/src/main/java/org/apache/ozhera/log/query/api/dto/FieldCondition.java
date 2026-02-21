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
import org.apache.ozhera.log.query.api.enums.OperatorEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Field condition for query filtering
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Field name
     */
    private String field;

    /**
     * Operator (EQ, NE, GT, LT, GTE, LTE, IN, LIKE, REGEX, etc.)
     */
    private OperatorEnum operator;

    /**
     * Field value (single value for EQ/NE/GT/LT/GTE/LTE/LIKE/REGEX)
     */
    private Object value;

    /**
     * Field values (for IN/NOT_IN operators)
     */
    private List<Object> values;

    /**
     * Logic connector with next condition (AND/OR), default is AND
     */
    @Builder.Default
    private LogicEnum logic = LogicEnum.AND;

    public enum LogicEnum {
        AND, OR
    }

    public static FieldCondition eq(String field, Object value) {
        return FieldCondition.builder()
                .field(field)
                .operator(OperatorEnum.EQ)
                .value(value)
                .build();
    }

    public static FieldCondition in(String field, List<Object> values) {
        return FieldCondition.builder()
                .field(field)
                .operator(OperatorEnum.IN)
                .values(values)
                .build();
    }

    public static FieldCondition like(String field, String pattern) {
        return FieldCondition.builder()
                .field(field)
                .operator(OperatorEnum.LIKE)
                .value(pattern)
                .build();
    }

    public static FieldCondition range(String field, Object from, Object to) {
        return FieldCondition.builder()
                .field(field)
                .operator(OperatorEnum.GTE)
                .value(from)
                .build();
    }
}
