package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.Elf;
import com.testcraftsmanship.deepassertions.core.base.testclasses.Location;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.config.Messages;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import org.junit.jupiter.api.BeforeAll;
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

public class DeepComparatorTest extends BaseTest {
    private static Config config;

    @BeforeAll
    public static void setUp() {
        config = new Config(DeepAssertType.LOCAL);
        config.setWithAnyOrder(false);
    }

    @Test
    public void onlyMarkedAsVerifiableShouldBeChecked() {
        Elf first = new Elf("Jan", "Laura");
        Elf second = new Elf("Jan", "Tom");
        LocationCreator locationCreator = new LocationCreator(config, first.getClass());
        DeepComparator deepComparator = new DeepComparator(config);
        deepComparator.compare(first, second, locationCreator);
    }

    @ParameterizedTest
    @MethodSource("objectWithFailuresMessage")
    public void deepAssertionsShouldIndicateAllFailures(Object actual, Object expected, List<String> expectedMessages) {
        LocationCreator locationCreator = new LocationCreator(config, actual.getClass());
        DeepComparator deepComparator = new DeepComparator(config);
        assertThatFunctionThrows(() -> deepComparator.compare(actual, expected, locationCreator), expectedMessages);
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

        LocationCreator locationCreator = new LocationCreator(config, locationA.getClass());
        DeepComparator deepComparator = new DeepComparator(config);
        assertThatThrownBy(() -> deepComparator.compare(locationA, locationB, locationCreator))
                .isInstanceOf(AssertionError.class)
                .hasMessageContainingAll(
                        "Multiple Failures (15 failures)",
                        String.format(Messages.DIFFERENT_VALUES, "Location.city", "String", "Lublin", "Gdansk"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.street", "String", "Krakowska", "Lubelska"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.streetNumber", "Integer", "120", "121"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.flatNumber", "Integer", "12", "0"),
                        String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.tags[]", "String[]", "are 2 items", "1"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.tags[0]", "String", "OLD", "NEW"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.tags[1]", "String", "RENOVATED", "null"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.numbers[2]", "Integer", "9", "8"),
                        String.format(Messages.DIFFERENCE_WITH_NO_EXPECTED_VALUE, "Location.roomNames()", "String", "Venus"),
                        String.format(Messages.DIFFERENCE_WITH_NO_ACTUAL_VALUE, "Location.roomNames()", "String", "Mercury"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.buildings(0).buildingName", "String", "Gym", "Offices"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.buildings(0).allRoomsNumber", "Integer", "4", "5"),
                        String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.buildings(0).roomsPerFlor()", "ImmutableMap", "are 4 items", "5"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.buildings(0).roomsPerFlor(5)", "Integer", "null", "2"),
                        String.format(Messages.DIFFERENT_VALUES, "Location.uuid", "UUID", "898496cc-2efd-47e3-8c41-76855c522fa7", "8f365f2d-c2be-4731-953c-4d1a9d75c0c7"));

    }

    private static Stream<Arguments> objectWithFailuresMessage() {
        return Stream.of(
                Arguments.of(new Elf("Ascal", "Holana"), new Elf("Arlen", "Holana"),
                        List.of("Multiple Failures (1 failure)",
                                String.format(Messages.DIFFERENT_VALUES, "Elf.firstName", "String", "Ascal", "Arlen"))),
                Arguments.of(List.of(new Elf("Raibyn", "Caimaris")), List.of(new Elf("Aymar", "Heleric")),
                        List.of("Multiple Failures (1 failure)",
                                String.format(Messages.DIFFERENT_VALUES, "List(0).firstName", "String", "Raibyn", "Aymar"))),
                Arguments.of(new Elf[]{new Elf("Iyrandrar", "Wysacan"), new Elf("Iyrandrar", "Wysacan")}, new Elf[]{new Elf("Falael", "Wysacan"), new Elf("Falael", "Wysacan")},
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_VALUES, "Elf[0].firstName", "String", "Iyrandrar", "Falael"),
                                String.format(Messages.DIFFERENT_VALUES, "Elf[1].firstName", "String", "Iyrandrar", "Falael"))),
                Arguments.of(Set.of(new Elf("Pywaln", "Reyrora")), Set.of(new Elf("Nieven", "Inakalyn")),
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENCE_WITH_NO_EXPECTED_VALUE, "ImmutableSet()", "Elf", "Pywaln Reyrora"),
                                String.format(Messages.DIFFERENCE_WITH_NO_ACTUAL_VALUE, "ImmutableSet()", "Elf", "Nieven Inakalyn"))),
                Arguments.of(Map.of("Lord", new Elf("Miirphys", "Keanan")), Map.of("Lord", new Elf("Ailuin", "Keanan")),
                        List.of("Multiple Failures (1 failure)",
                                String.format(Messages.DIFFERENT_VALUES, "ImmutableMap(Lord).firstName", "String", "Miirphys", "Ailuin")))
        );
    }
}
