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
 * Full text search mode enumeration
 */
@Getter
public enum FullTextModeEnum {

    MATCH("match", "Standard match query"),
    PHRASE("phrase", "Phrase match query"),
    PHRASE_PREFIX("phrase_prefix", "Phrase prefix match query"),
    WILDCARD("wildcard", "Wildcard query"),
    REGEX("regex", "Regular expression query"),
    SIMPLE_QUERY_STRING("simple_query_string", "Simple query string");

    private final String code;
    private final String description;

    FullTextModeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static FullTextModeEnum fromCode(String code) {
        if (code == null) {
            return MATCH;
        }
        for (FullTextModeEnum mode : values()) {
            if (mode.code.equalsIgnoreCase(code)) {
                return mode;
            }
        }
        return MATCH;
    }
}
