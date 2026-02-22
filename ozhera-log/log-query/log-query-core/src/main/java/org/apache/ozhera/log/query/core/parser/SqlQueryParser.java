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

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ozhera.log.query.api.dto.FieldCondition;
import org.apache.ozhera.log.query.api.dto.UnifiedLogQuery;
import org.apache.ozhera.log.query.api.enums.OperatorEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL query parser implementation using JSqlParser
 * Converts SQL SELECT statements to UnifiedLogQuery
 */
@Slf4j
public class SqlQueryParser implements QueryParser {

    private static final String PARSER_TYPE = "SQL";

    @Override
    public UnifiedLogQuery parse(String queryString) throws QueryParseException {
        if (queryString == null || queryString.trim().isEmpty()) {
            throw new QueryParseException("Query string cannot be empty", queryString, PARSER_TYPE);
        }

        try {
            Statement statement = CCJSqlParserUtil.parse(queryString);

            if (!(statement instanceof Select)) {
                throw new QueryParseException("Only SELECT statements are supported", queryString, PARSER_TYPE);
            }

            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            return parseSelect(plainSelect, queryString);
        } catch (JSQLParserException e) {
            throw new QueryParseException("Failed to parse SQL: " + e.getMessage(), queryString, PARSER_TYPE, e);
        }
    }

    @Override
    public boolean supports(String queryString) {
        if (queryString == null || queryString.trim().isEmpty()) {
            return false;
        }
        String trimmed = queryString.trim().toUpperCase();
        return trimmed.startsWith("SELECT ");
    }

    @Override
    public String getParserType() {
        return PARSER_TYPE;
    }

    private UnifiedLogQuery parseSelect(PlainSelect plainSelect, String originalQuery) {
        UnifiedLogQuery.UnifiedLogQueryBuilder builder = UnifiedLogQuery.builder();

        // Parse WHERE clause
        if (plainSelect.getWhere() != null) {
            List<FieldCondition> conditions = new ArrayList<>();
            parseWhereExpression(plainSelect.getWhere(), conditions, builder);
            if (!conditions.isEmpty()) {
                builder.conditions(conditions);
            }
        }

        // Parse ORDER BY
        if (plainSelect.getOrderByElements() != null && !plainSelect.getOrderByElements().isEmpty()) {
            OrderByElement orderBy = plainSelect.getOrderByElements().get(0);
            builder.sortField(orderBy.getExpression().toString());
            builder.ascending(orderBy.isAsc());
        }

        // Parse LIMIT
        if (plainSelect.getLimit() != null) {
            Limit limit = plainSelect.getLimit();
            if (limit.getRowCount() != null) {
                builder.pageSize(extractIntValue(limit.getRowCount()));
            }
            if (limit.getOffset() != null) {
                int offset = extractIntValue(limit.getOffset());
                int pageSize = builder.build().getPageSize();
                if (pageSize > 0) {
                    builder.page((offset / pageSize) + 1);
                }
            }
        }

        return builder.build();
    }

    private void parseWhereExpression(Expression expression, List<FieldCondition> conditions,
                                       UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        if (expression instanceof AndExpression) {
            AndExpression and = (AndExpression) expression;
            parseWhereExpression(and.getLeftExpression(), conditions, builder);
            parseWhereExpression(and.getRightExpression(), conditions, builder);
        } else if (expression instanceof OrExpression) {
            OrExpression or = (OrExpression) expression;
            // For OR expressions, we add them as separate conditions with OR logic
            List<FieldCondition> orConditions = new ArrayList<>();
            parseWhereExpression(or.getLeftExpression(), orConditions, builder);
            parseWhereExpression(or.getRightExpression(), orConditions, builder);
            // Mark last condition as OR
            if (!orConditions.isEmpty()) {
                for (int i = 0; i < orConditions.size() - 1; i++) {
                    orConditions.get(i).setLogic(FieldCondition.LogicEnum.OR);
                }
                conditions.addAll(orConditions);
            }
        } else if (expression instanceof Parenthesis) {
            parseWhereExpression(((Parenthesis) expression).getExpression(), conditions, builder);
        } else {
            FieldCondition condition = parseCondition(expression, builder);
            if (condition != null) {
                conditions.add(condition);
            }
        }
    }

    private FieldCondition parseCondition(Expression expression, UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        // Handle comparison operators
        if (expression instanceof ComparisonOperator) {
            return parseComparisonOperator((ComparisonOperator) expression, builder);
        }

        // Handle BETWEEN
        if (expression instanceof Between) {
            return parseBetween((Between) expression, builder);
        }

        // Handle IN
        if (expression instanceof InExpression) {
            return parseInExpression((InExpression) expression);
        }

        // Handle LIKE
        if (expression instanceof LikeExpression) {
            return parseLikeExpression((LikeExpression) expression, builder);
        }

        // Handle IS NULL / IS NOT NULL
        if (expression instanceof IsNullExpression) {
            return parseIsNullExpression((IsNullExpression) expression);
        }

        log.debug("Unsupported expression type: {}", expression.getClass().getSimpleName());
        return null;
    }

    private FieldCondition parseComparisonOperator(ComparisonOperator comp,
                                                    UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        String field = extractFieldName(comp.getLeftExpression());
        Object value = extractValue(comp.getRightExpression());

        if (field == null) {
            return null;
        }

        // Handle special fields
        if (handleSpecialField(field, value, comp.getStringExpression(), builder)) {
            return null;
        }

        OperatorEnum operator = mapComparisonOperator(comp.getStringExpression());

        return FieldCondition.builder()
                .field(field)
                .operator(operator)
                .value(value)
                .build();
    }

    private boolean handleSpecialField(String field, Object value, String operator,
                                        UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        String fieldLower = field.toLowerCase();

        // Handle timestamp field
        if ("timestamp".equals(fieldLower)) {
            Long timeValue = extractLongValue(value);
            if (timeValue != null) {
                if (">=".equals(operator) || ">".equals(operator)) {
                    builder.startTime(timeValue);
                } else if ("<=".equals(operator) || "<".equals(operator)) {
                    builder.endTime(timeValue);
                }
            }
            return true;
        }

        // Handle storeId field
        if ("storeid".equals(fieldLower) && "=".equals(operator)) {
            builder.storeId(extractLongValue(value));
            return true;
        }

        // Handle tail field
        if ("tail".equals(fieldLower) && "=".equals(operator)) {
            builder.tailNames(String.valueOf(value));
            return true;
        }

        // Handle tailId field
        if ("tailid".equals(fieldLower) && "=".equals(operator)) {
            Long tailId = extractLongValue(value);
            if (tailId != null) {
                builder.tailIds(List.of(tailId));
            }
            return true;
        }

        return false;
    }

    private FieldCondition parseBetween(Between between, UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        String field = extractFieldName(between.getLeftExpression());
        if (field == null) {
            return null;
        }

        // Handle timestamp BETWEEN specially
        if ("timestamp".equalsIgnoreCase(field)) {
            Long start = extractLongValue(extractValue(between.getBetweenExpressionStart()));
            Long end = extractLongValue(extractValue(between.getBetweenExpressionEnd()));
            if (start != null) builder.startTime(start);
            if (end != null) builder.endTime(end);
            return null;
        }

        // For other fields, convert to GTE condition (simplified)
        return FieldCondition.builder()
                .field(field)
                .operator(OperatorEnum.GTE)
                .value(extractValue(between.getBetweenExpressionStart()))
                .build();
    }

    private FieldCondition parseInExpression(InExpression in) {
        String field = extractFieldName(in.getLeftExpression());
        if (field == null) {
            return null;
        }

        List<Object> values = new ArrayList<>();
        if (in.getRightItemsList() instanceof ExpressionList) {
            ExpressionList exprList = (ExpressionList) in.getRightItemsList();
            for (Expression expr : exprList.getExpressions()) {
                values.add(extractValue(expr));
            }
        }

        return FieldCondition.builder()
                .field(field)
                .operator(in.isNot() ? OperatorEnum.NOT_IN : OperatorEnum.IN)
                .values(values)
                .build();
    }

    private FieldCondition parseLikeExpression(LikeExpression like, UnifiedLogQuery.UnifiedLogQueryBuilder builder) {
        String field = extractFieldName(like.getLeftExpression());
        String pattern = extractStringValue(like.getRightExpression());

        if (field == null || pattern == null) {
            return null;
        }

        // Handle message field as full text search
        if ("message".equalsIgnoreCase(field)) {
            // Remove % wildcards for full text search
            String searchText = pattern.replace("%", "").trim();
            if (!searchText.isEmpty()) {
                builder.fullTextSearch(searchText);
            }
            return null;
        }

        return FieldCondition.builder()
                .field(field)
                .operator(like.isNot() ? OperatorEnum.NOT_LIKE : OperatorEnum.LIKE)
                .value(pattern)
                .build();
    }

    private FieldCondition parseIsNullExpression(IsNullExpression isNull) {
        String field = extractFieldName(isNull.getLeftExpression());
        if (field == null) {
            return null;
        }

        return FieldCondition.builder()
                .field(field)
                .operator(isNull.isNot() ? OperatorEnum.EXISTS : OperatorEnum.NOT_EXISTS)
                .build();
    }

    // ==================== Helper Methods ====================

    private String extractFieldName(Expression expression) {
        if (expression instanceof Column) {
            return ((Column) expression).getColumnName();
        }
        return null;
    }

    private Object extractValue(Expression expression) {
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        }
        if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue();
        }
        if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue();
        }
        if (expression instanceof NullValue) {
            return null;
        }
        if (expression instanceof SignedExpression) {
            SignedExpression signed = (SignedExpression) expression;
            Object value = extractValue(signed.getExpression());
            if (signed.getSign() == '-' && value instanceof Number) {
                if (value instanceof Long) {
                    return -((Long) value);
                } else if (value instanceof Double) {
                    return -((Double) value);
                }
            }
            return value;
        }
        // Return string representation for other types
        return expression.toString();
    }

    private String extractStringValue(Expression expression) {
        Object value = extractValue(expression);
        return value != null ? value.toString() : null;
    }

    private Long extractLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int extractIntValue(Expression expression) {
        Object value = extractValue(expression);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private OperatorEnum mapComparisonOperator(String operator) {
        return switch (operator) {
            case "=" -> OperatorEnum.EQ;
            case "!=" , "<>" -> OperatorEnum.NE;
            case ">" -> OperatorEnum.GT;
            case ">=" -> OperatorEnum.GTE;
            case "<" -> OperatorEnum.LT;
            case "<=" -> OperatorEnum.LTE;
            default -> OperatorEnum.EQ;
        };
    }
}
