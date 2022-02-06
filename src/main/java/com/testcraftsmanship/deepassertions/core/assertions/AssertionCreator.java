package com.testcraftsmanship.deepassertions.core.assertions;

import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.List;

public class AssertionCreator {
    private SoftAssertions assertions;
    private List<String> assertionMessages;

    public AssertionCreator() {
        this.assertions  = new SoftAssertions();
        this.assertionMessages = new ArrayList<>();
    }

    public void performAssertions() {
        assertions.assertAll();
    }

    public void fail(String failureMessage, Object... args) {
        assertions.fail(failureMessage, args);
        assertionMessages.add(String.format(failureMessage, args));
    }
}
