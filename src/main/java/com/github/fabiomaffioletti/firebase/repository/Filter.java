package com.github.fabiomaffioletti.firebase.repository;

import com.google.common.collect.ImmutableMap;

import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.isEmpty;

public class Filter {
    private ImmutableMap<String, Object> conditions;

    private Filter(ImmutableMap<String, Object> conditions) {
        this.conditions = conditions;
    }

    public String toQueryParameters() {
        return conditions.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(joining("&", "?", ""));
    }

    public static class FilterBuilder {

        private String orderBy;

        private Integer limitToFirst;

        private Integer limitToLast;

        private Object startAt;

        private Object endAt;

        private Object equalTo;

        private FilterBuilder() {}

        public static FilterBuilder builder() {
            return new FilterBuilder();
        }

        public FilterBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public FilterBuilder limitToFirst(Integer limitToFirst) {
            this.limitToFirst = limitToFirst;
            return this;
        }

        public FilterBuilder limitToLast(Integer limitToLast) {
            this.limitToLast = limitToLast;
            return this;
        }

        public FilterBuilder startAt(Object startAt) {
            this.startAt = checkAndApplyType(startAt);
            return this;
        }

        public FilterBuilder endAt(Object endAt) {
            this.endAt = checkAndApplyType(endAt);
            return this;
        }

        public FilterBuilder equalTo(Object equalTo) {
            this.equalTo = checkAndApplyType(equalTo);
            return this;
        }

        public Filter build() {
            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
            if (!isEmpty(orderBy)) builder.put("orderBy", "\"" + orderBy + "\"");
            if (!isEmpty(limitToFirst)) builder.put("limitToFirst", String.valueOf(limitToFirst));
            if (!isEmpty(limitToLast)) builder.put("limitToLast", String.valueOf(limitToLast));
            if (!isEmpty(startAt)) builder.put("startAt", startAt);
            if (!isEmpty(endAt)) builder.put("endAt", endAt);
            if (!isEmpty(equalTo)) builder.put("equalTo", equalTo);
            return new Filter(builder.build());
        }

        private Object checkAndApplyType(Object object) {
            if (object instanceof String) {
                return "\"" + object + "\"";
            }
            return object;
        }

    }

}