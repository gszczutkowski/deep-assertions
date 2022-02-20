package com.testcraftsmanship.deepassertions.core.api;

/**
 * DEFINED - then assertion requires keyword, only annotations with this keyword will be taken
 * into consideration in deep assertion
 * LOCAL - no annotations needed all object from project package will be taken into consideration
 * in deep assertion
 * ANNOTATED - no keywords needed, all marked with annotation @DeepVerifiable will be taken
 * into consideration in deep assertion
 */
public enum DeepAssertType {
    DEFINED, LOCAL, ANNOTATED
}
