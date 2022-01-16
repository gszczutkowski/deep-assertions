package com.testcraftsmanship.core;

import com.testcraftsmanship.core.annotations.Verifiable;
import com.testcraftsmanship.core.api.DeepAssertions;
import com.testcraftsmanship.core.type.ReflectAssertionType;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeepAssertionsTest {
    @AllArgsConstructor
    static class A {
        @Verifiable(type = {"user"})
        private String first;
        private String second;
    }

    @Test
    public void onlyMarkedAsVerifiableShouldBeChecked() {
        A first = new A("Jan", "Laura");
        A second = new A("Jan", "Tom");
        DeepAssertions deepAssertions = new DeepAssertions();
        deepAssertions.assertEqualityOfAnnotatedFields(first, second, ReflectAssertionType.USER);
    }

    @Test
    public void onlyMarkedAsVerifiableShouldBeCheckedNegative() {
        A first = new A("Artur", "Laura");
        A second = new A("Jan", "Tom");
        DeepAssertions deepAssertions = new DeepAssertions();
        assertThatThrownBy(() -> {
            deepAssertions.assertEqualityOfAnnotatedFields(first, second, ReflectAssertionType.USER);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContainingAll("Multiple Failures (1 failure)",
                        "A: String first has value Artur, expect to have Jan");
    }
}
