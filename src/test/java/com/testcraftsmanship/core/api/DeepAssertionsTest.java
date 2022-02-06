package com.testcraftsmanship.core.api;

import com.testcraftsmanship.core.testclass.Location;
import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import lombok.AllArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeepAssertionsTest {

    @AllArgsConstructor
    static class Elf {
        @Verifiable(type = {"user"})
        private String firstName;
        private String lastName;

        @Override
        public String toString() {
            return firstName + " " + lastName;
        }
    }

    @Test
    public void onlyMarkedAsVerifiableShouldBeChecked() {
        Elf first = new Elf("Jan", "Laura");
        Elf second = new Elf("Jan", "Tom");
        DeepAssertions deepAssertions = new DeepAssertions();
        deepAssertions.assertEquals(first, second);
    }

    @ParameterizedTest
    @MethodSource("objectWithFailuresMessage")
    public void deepAssertionsShouldIndicateAllFailures(Object actual, Object expected, List<String> expectedMessages) {
        DeepAssertions deepAssertions = new DeepAssertions();
        assertThatFunctionThrows(() -> deepAssertions.assertEquals(actual, expected), expectedMessages);
    }

    @Test
    public void deepAssertionsShouldIndicateAllFailuresInComplexClass() {
        Location locationA = Location.builder().city("Lublin")
                .street("Krakowska")
                .streetNumber(120)
                .flatNumber(12)
                .numbers(new int[]{1, 6, 9})
                .tags(new String[]{"OLD", "RENOVATED"})
                .roomNames(Set.of("Sun", "Venus"))
                .buildings(List.of(Location.Building.builder()
                        .buildingName("Gym")
                        .allRoomsNumber(4)
                        .roomsPerFlor(Map.of(1, 3, 2, 5, 3, 4, 4, 2))
                        .build()))
                .uuid(UUID.fromString("898496cc-2efd-47e3-8c41-76855c522fa7"))
                .build();

        Location locationB = Location.builder().city("Gdansk")
                .street("Lubelska")
                .streetNumber(121)
                .flatNumber(0)
                .numbers(new int[]{1, 6, 8})
                .tags(new String[]{"NEW"})
                .roomNames(Set.of("Sun", "Mercury"))
                .buildings(List.of(Location.Building.builder()
                        .buildingName("Offices")
                        .allRoomsNumber(5)
                        .roomsPerFlor(Map.of(1, 3, 2, 5, 3, 4, 4, 2, 5, 2))
                        .build()))
                .uuid(UUID.fromString("8f365f2d-c2be-4731-953c-4d1a9d75c0c7"))
                .build();

        DeepAssertions deepAssertions = new DeepAssertions();
        assertThatThrownBy(() -> {
            deepAssertions.assertEquals(locationA, locationB);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContainingAll(
                        "Multiple Failures (12 failures)",
                        "Location.city<String> - actual object has value Lublin, expect to have Gdansk",
                        "Location.street<String> - actual object has value Krakowska, expect to have Lubelska",
                        "Location.streetNumber<Integer> - actual object has value 120, expect to have 121",
                        "Location.flatNumber<Integer> - actual object has value 12, expect to have 0",
                        "Location.tags[]<String[]> - actual object has size 2 but expected to have size 1",
                        "Location.numbers[2]<Integer> - actual object has value 9, expect to have 8",
                        "Location.roomNames()<String> - actual set has value {Venus}, expected set don't have it",
                        "Location.roomNames()<String> - expected set has value {Mercury}, actual set don't have it",
                        "Location.buildings(0).buildingName<String> - actual object has value Gym, expect to have Offices",
                        "Location.buildings(0).allRoomsNumber<Integer> - actual object has value 4, expect to have 5",
                        "Location.buildings(0).roomsPerFlor()<ImmutableMap> - actual object has size 4 but expected to have size 5",
                        "Location.uuid<UUID> - actual object has value 898496cc-2efd-47e3-8c41-76855c522fa7, expect to have 8f365f2d-c2be-4731-953c-4d1a9d75c0c7");

    }

    private static Stream<Arguments> objectWithFailuresMessage() {
        return Stream.of(
                Arguments.of(new Elf("Ascal", "Holana"), new Elf("Arlen", "Holana"),
                        List.of("Multiple Failures (1 failure)", "Elf.firstName<String> - actual object has value Ascal, expect to have Arlen")),
                Arguments.of(List.of(new Elf("Raibyn", "Caimaris")), List.of(new Elf("Aymar", "Heleric")),
                        List.of("Multiple Failures (1 failure)", "List(0).firstName<String> - actual object has value Raibyn, expect to have Aymar")),
                Arguments.of(new Elf[]{new Elf("Iyrandrar", "Wysacan"), new Elf("Iyrandrar", "Wysacan")}, new Elf[]{new Elf("Falael", "Wysacan"), new Elf("Falael", "Wysacan")},
                        List.of("Multiple Failures (2 failures)",
                                "Elf[0].firstName<String> - actual object has value Iyrandrar, expect to have Falael",
                                "Elf[1].firstName<String> - actual object has value Iyrandrar, expect to have Falael")),
                Arguments.of(Set.of(new Elf("Pywaln", "Reyrora")), Set.of(new Elf("Nieven", "Inakalyn")),
                        List.of("Multiple Failures (2 failures)",
                                "ImmutableSet()<Elf> - actual set has value {Pywaln Reyrora}, expected set don't have it",
                                "ImmutableSet()<Elf> - expected set has value {Nieven Inakalyn}, actual set don't have it")),
                Arguments.of(Map.of("Lord", new Elf("Miirphys", "Keanan")), Map.of("Lord", new Elf("Ailuin", "Keanan")),
                        List.of("Multiple Failures (1 failure)",
                                "ImmutableMap(Lord).firstName<String> - actual object has value Miirphys, expect to have Ailuin"))
        );
    }

    private void assertThatFunctionThrows(ThrowableAssert.ThrowingCallable shouldRaiseThrowable, List<String> messages) {
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
