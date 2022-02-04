package com.testcraftsmanship.core;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import com.testcraftsmanship.core.testclass.Location;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
        deepAssertions.assertEquals(first, second);
    }

    @Test
    public void onlyMarkedAsVerifiableShouldBeCheckedNegative() {
        A first = new A("Artur", "Laura");
        A second = new A("Jan", "Tom");
        DeepAssertions deepAssertions = new DeepAssertions();
        assertThatThrownBy(() -> {
            deepAssertions.assertEquals(first, second);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContainingAll("Multiple Failures (1 failure)",
                        "String A.first has value Artur, expect to have Jan");
    }

    @Test
    public void deepTest() {
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
                        .roomsPerFlor(Map.of(1, 3, 2, 5, 3, 4,4, 2))
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
                        .roomsPerFlor(Map.of(1, 3, 2, 5, 3, 4,4, 2, 5, 2))
                        .build()))
                .uuid(UUID.fromString("8f365f2d-c2be-4731-953c-4d1a9d75c0c7"))
                .build();

        DeepAssertions deepAssertions = new DeepAssertions();
        assertThatThrownBy(() -> {
            deepAssertions.assertEquals(locationA, locationB);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContainingAll(
                        "Multiple Failures (11 failures)",
                        "String Location.city has value Lublin, expect to have Gdansk",
                        "String Location.street has value Krakowska, expect to have Lubelska",
                        "Integer Location.streetNumber has value 120, expect to have 121",
                        "Integer Location.flatNumber has value 12, expect to have 0",
                        "Location.tags expect to have 1 items but have 2 items",
                        "Integer Location.numbers[2] has value 9, expect to have 8",
                        "String Location.roomNames(0) has value Venus, expect to have Mercury",
                        "String Location.buildings(0).buildingName has value Gym, expect to have Offices",
                        "Integer Location.buildings(0).allRoomsNumber has value 4, expect to have 5",
                        "MapN Location.buildings(0).roomsPerFlor has value {1=3, 2=5, 3=4, 4=2}, expect to have {1=3, 2=5, 3=4, 4=2, 5=2}",
                        "UUID Location.uuid has value 898496cc-2efd-47e3-8c41-76855c522fa7, expect to have 8f365f2d-c2be-4731-953c-4d1a9d75c0c7");

    }
}
