package de.fraunhofer.abm.crawler.api;

import java.util.ArrayList;
import java.util.List;

public class Criteria {

    public static enum KEY {
        LANGUAGE,
        LICENSE,
        SIZE,
        TYPE
    }

    public static enum OP {
        EQUAL,
        LESS_THAN,
        LESS_THAN_EQUAL,
        GREATER_THAN,
        GREATER_THAN_EQUAL
    }

    private List<Criterion> includes = new ArrayList<>();
    private List<Criterion> excludes = new ArrayList<>();

    private Criteria(KEY key, OP operand, String value) {
        includes.add(new Criterion(key, operand, value));
    }

    public static Criteria with(KEY key, OP operand, String value) {
        Criteria criteria = new Criteria(key, operand, value);
        return criteria;
    }

    private class Criterion {
        KEY key; OP operand; String value;
        public Criterion(KEY key, OP operand, String value) {
            super();
            this.key = key;
            this.operand = operand;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + " " + operand + " " + value;
        }
    }

    @Override
    public String toString() {
        return "Includes:" + includes + " Excludes:" + excludes;
    }
}
