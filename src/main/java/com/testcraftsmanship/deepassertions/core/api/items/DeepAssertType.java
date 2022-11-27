package com.testcraftsmanship.deepassertions.core.api.items;

/**
 * LOCAL - no annotations needed all object from project package will be taken into consideration
 * in deep assertion
 * ANNOTATED - no keywords needed, all marked with annotation @DeepVerifiable will be taken
 * into consideration in deep assertion
 */
public enum DeepAssertType {
    LOCAL, ANNOTATED;

    public static DeepAssertType parse(String type) {
        if (type.toLowerCase().matches("local")) {
            return LOCAL;
        } else if (type.toLowerCase().matches("annotated")) {
            return ANNOTATED;
        } else {
            throw new IllegalArgumentException("There is no assert type matching " + type);
        }
    }
}
