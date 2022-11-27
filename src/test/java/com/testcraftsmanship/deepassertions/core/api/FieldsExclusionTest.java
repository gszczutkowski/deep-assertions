package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.annotations.VerifiableExclude;
import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Disabled
public class FieldsExclusionTest extends BaseTest {

    @Test
    public void excludedFieldsShouldNotBeTakenIntoConsiderationInComparison() {
        A1 actual = new A1("Gandalf", 10);
        A1 expected = new A1("Dumbledore", 5);
        assertThatFunctionThrows(
                () -> DeepAssertions
                        .assertThat(actual)
                        .isEqualTo(expected),
                List.of("Multiple Failures (1 failure)",
                        "-- failure 1 --A1.a<String> - actual object has value {Gandalf}, expect to have {Dumbledore}"));
    }

    @Test
    public void includedFieldsShouldBeTakenIntoConsiderationInComparison() {
        A2 actual = new A2("Gandalf", 10);
        A2 expected = new A2("Dumbledore", 5);
        assertThatFunctionThrows(
                () -> DeepAssertions
                        .assertThat(actual)
                        .withExcludingAllFieldsByDefault()
                        .isEqualTo(expected),
                List.of("Multiple Failures (1 failure)",
                        "-- failure 1 --A2.b<Integer> - actual object has value {10}, expect to have {5}"));
    }

    @Test
    public void includedListsShouldBeTakenIntoConsiderationInComparison() {
        B1 actual = new B1(List.of("Gandalf"), List.of("Mage"));
        B1 expected = new B1(List.of("Dumbledore"), List.of("Teacher"));
        assertThatFunctionThrows(
                () -> DeepAssertions
                        .assertThat(actual)
                        .withExcludingAllFieldsByDefault()
                        .isEqualTo(expected),
                List.of("Multiple Failures (1 failure)",
                        "-- failure 1 --B1.listB()<ImmutableList<String>> - actual object has value {[Mage]}, expect to have {[Teacher]}"));
    }

    @Test
    public void includedListsShouldBeTakenIntoConsiderationInComparison_change_name_1() {
        B1 actual = new B1(List.of("Gandalf"), List.of("Mage"));
        B1 expected = new B1(List.of("Dumbledore"), List.of());
        assertThatFunctionThrows(
                () -> DeepAssertions
                        .assertThat(actual)
                        .withExcludingAllFieldsByDefault()
                        .isEqualTo(expected),
                List.of("Multiple Failures (1 failure)",
                        "-- failure 1 --B1.listB()<ImmutableList<String>> - actual object has value {[Mage]}, expect to have {[]}"));
    }

    @Test
    public void includedListsShouldBeTakenIntoConsiderationInComparison_change_name_2() {
        B1 actual = new B1(List.of("Gandalf"), List.of("Mage"));
        B1 expected = new B1(List.of("Dumbledore"), Set.of("Mage"));
        assertThatFunctionThrows(
                () -> DeepAssertions
                        .assertThat(actual)
                        .withExcludingAllFieldsByDefault()
                        .isEqualTo(expected),
                List.of("Multiple Failures (1 failure)",
                        "-- failure 1 --B1.listB() - actual object type is <ImmutableList>, expected type is <ImmutableSet>"));
    }
}

@EqualsAndHashCode
@AllArgsConstructor
class A1 {
    String a;
    @VerifiableExclude
    int b;
}

@EqualsAndHashCode
@AllArgsConstructor
class A2 {
    String a;
    @Verifiable
    int b;
}

@EqualsAndHashCode
@AllArgsConstructor
class B1 {
    List<String> listA;
    @Verifiable
    Collection<String> listB;
}
