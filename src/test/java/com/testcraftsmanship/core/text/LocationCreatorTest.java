package com.testcraftsmanship.core.text;

import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import com.testcraftsmanship.deepassertions.core.text.MessageCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static com.testcraftsmanship.core.text.MessageCreatorTest.Color.RED;
import static org.assertj.core.api.Assertions.assertThat;

public class LocationCreatorTest {
    @ParameterizedTest
    @MethodSource("objectToLocation")
    public void classNameShouldBeExtractedCorrectly(Object object, String expectedLocation) {
        String actualLocation = LocationCreator.classNameExtractor(object.getClass());
        assertThat(actualLocation).isEqualTo(expectedLocation);
    }

    @ParameterizedTest
    @MethodSource("objectWithFieldToLocation")
    public void classNameShouldBeExtractedCorrectly(Object rootObject, Field field, String expectedLocation) {
        LocationCreator locationCreator = new LocationCreator(rootObject.getClass());
        String actualLocation = locationCreator.locationOfField(field).getLocation();
        assertThat(actualLocation).isEqualTo(expectedLocation);
    }

    private static Stream<Arguments> objectToLocation() {
        return Stream.of(
                Arguments.of(List.of(), "ImmutableList"),
                Arguments.of(new ArrayList(), "ArrayList"),
                Arguments.of(new HashMap<>(), "HashMap"),
                Arguments.of(new HashSet<>(), "HashSet"),
                Arguments.of(new int[1], "int[]"),
                Arguments.of("Text", "String"),
                Arguments.of((short) 10, "Short"),
                Arguments.of('a', "Character"),
                Arguments.of(RED, "Color"),
                Arguments.of(new Item(), "Item"),
                Arguments.of(new Date(), "Date")
        );
    }

    private static Stream<Arguments> objectWithFieldToLocation() throws NoSuchFieldException {
        return Stream.of(
                Arguments.of(new Item(), new Item().getClass().getDeclaredField("name"), "Item.name"),
                Arguments.of(new Item(), new Item().getClass().getDeclaredField("namesList"), "Item.namesList()"),
                Arguments.of(new Item(), new Item().getClass().getDeclaredField("namesMap"), "Item.namesMap()"),
                Arguments.of(new Item(), new Item().getClass().getDeclaredField("numbers"), "Item.numbers[]")
        );
    }

    static class Item {
        String name = "Jane";
        List<String> namesList = List.of("Jussi", "Arthur");
        Map<String, String> namesMap = Map.of("Tom", "Manager");
        int[] numbers = {1};
    }
}
