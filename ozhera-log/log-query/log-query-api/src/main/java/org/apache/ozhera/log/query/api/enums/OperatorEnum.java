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
package org.apache.ozhera.log.query.api.enums;

import lombok.Getter;

/**
 * Field condition operator enumeration
 */
@Getter
public enum OperatorEnum {

    EQ("=", "equals"),
    NE("!=", "not equals"),
    GT(">", "greater than"),
    GTE(">=", "greater than or equals"),
    LT("<", "less than"),
    LTE("<=", "less than or equals"),
    IN("IN", "in list"),
    NOT_IN("NOT IN", "not in list"),
    LIKE("LIKE", "like pattern"),
    NOT_LIKE("NOT LIKE", "not like pattern"),
    REGEX("REGEX", "regular expression"),
    EXISTS("EXISTS", "field exists"),
    NOT_EXISTS("NOT EXISTS", "field not exists");

    private final String symbol;
    private final String description;

    OperatorEnum(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public static OperatorEnum fromSymbol(String symbol) {
        if (symbol == null) {
            return EQ;
        }
        for (OperatorEnum op : values()) {
            if (op.symbol.equalsIgnoreCase(symbol)) {
                return op;
            }
        }
        return EQ;
    }
}
