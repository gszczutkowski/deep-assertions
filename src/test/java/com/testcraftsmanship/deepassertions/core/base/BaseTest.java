package com.testcraftsmanship.deepassertions.core.base;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;

import java.util.List;

public class BaseTest {
    protected void assertThatFunctionThrows(ThrowableAssert.ThrowingCallable shouldRaiseThrowable, List<String> messages) {
        String thrownMessage;
        try {
            shouldRaiseThrowable.call();
            throw new AssertionError("Expecting code to raise a throwable.");
        } catch (Throwable assertionError) {
            thrownMessage = assertionError.getMessage();
        }
        SoftAssertions softAssertions = new SoftAssertions();
        for (String message : messages) {
            softAssertions.assertThat(thrownMessage).contains(message);
        }
        softAssertions.assertAll();
    }
}
