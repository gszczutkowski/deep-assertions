package com.testcraftsmanship.deepassertions.core.config;

public final class Messages {
    private Messages() {
    }

    public static final String DIFFERENT_NUMBER_WITH_VALUE = "%s%s - there %s with value %s, expected to have %s";
    public static final String DIFFERENT_COLLECTIONS_SIZES = "%s<%s> -  there %s in actual object, expected to have %s";
    public static final String DIFFERENT_VALUES = "%s<%s> - actual object has value {%s}, expect to have {%s}";
    public static final String DIFFERENT_TYPES = "%s - actual object type is <%s>, expected type is <%s>";
    public static final String DIFFERENCE_WITH_NO_EXPECTED_VALUE
            = "%s<%s> - actual object contains value {%s}, expected one don't have it";
    public static final String DIFFERENCE_WITH_NO_ACTUAL_VALUE
            = "%s<%s> - expected object contains value {%s}, actual aon don't have it";
}
