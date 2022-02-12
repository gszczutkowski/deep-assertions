package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.annotations.VerifiableExclude;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableForType;
import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableOnClassLevel;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ObjectValidatorTest {
    static class TestClass {
        @Verifiable
        String annotatedTestString = "Test";
        String testString = "Test";
    }

    @Verifiable
    static class AnnotatedTestClass {
        @VerifiableExclude
        String excludedTestString = "Test";
        String testString = "Test";
    }

    @Test
    public void notAnnotatedShouldNotBeVerifiable() throws NoSuchFieldException {
        TestClass testClass = new TestClass();
        Field stringField = testClass.getClass().getDeclaredField("testString");
        assertThat(isApiVerifiableForType(stringField)).isFalse();
    }

    @Test
    public void annotatedShouldBeVerifiable() throws NoSuchFieldException {
        TestClass testClass = new TestClass();
        Field stringField = testClass.getClass().getDeclaredField("annotatedTestString");
        assertThat(isApiVerifiableForType(stringField)).isTrue();
    }

    @Test
    public void classAnnotatedExcludedShouldNotBeVerifiable() throws NoSuchFieldException {
        AnnotatedTestClass testClass = new AnnotatedTestClass();
        Field stringField = testClass.getClass().getDeclaredField("excludedTestString");
        assertThat(isApiVerifiableOnClassLevel(testClass.getClass(), stringField)).isFalse();
    }

    @Test
    public void classAnnotatedShouldBeVerifiable() throws NoSuchFieldException {
        AnnotatedTestClass testClass = new AnnotatedTestClass();
        Field stringField = testClass.getClass().getDeclaredField("testString");
        assertThat(isApiVerifiableOnClassLevel(testClass.getClass(), stringField)).isTrue();
    }

}
