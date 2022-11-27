package com.testcraftsmanship.deepassertions.core.text;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationCreatorTest {

    @ParameterizedTest
    @MethodSource("classToLocation")
    public void classNameShouldBeNormalizedCorrectly(Class clazz, String expectedLocation) {
        String actualLocation = LocationCreator.normalizeClassName(clazz);
        assertThat(actualLocation).isEqualTo(expectedLocation);
    }

    @ParameterizedTest
    @MethodSource("objectToLocation")
    public void classNameShouldBeExtractedCorrectly(Object object, String expectedLocation) {
        String actualLocation = LocationCreator.extractItemClassName(object);
        assertThat(actualLocation).isEqualTo(expectedLocation);
    }

    @ParameterizedTest
    @MethodSource("objectWithFieldToLocation")
    public void classNameShouldBeExtractedCorrectly(Object rootObject, Field field, String expectedLocation) {
        LocationCreator locationCreator = new LocationCreator(rootObject.getClass());
        String actualLocation = locationCreator.locationOfField(field).getLocation();
        assertThat(actualLocation).isEqualTo(expectedLocation);
    }

    private static Stream<Arguments> classToLocation() {
        return Stream.of(
                Arguments.of(List.of().getClass(), "ImmutableList"),
                Arguments.of(new ArrayList<>().getClass(), "ArrayList"),
                Arguments.of(new HashMap<>().getClass(), "HashMap"),
                Arguments.of(new HashSet<>().getClass(), "HashSet"),
                Arguments.of(new int[1].getClass(), "int[]"),
                Arguments.of("Text".getClass(), "String"),
                Arguments.of(((Object)10L).getClass(), "Long"),
                Arguments.of(((Object)'a').getClass(), "Character"),
                Arguments.of(MessageCreatorTest.Color.RED.getClass(), "Color"),
                Arguments.of(new Item().getClass(), "Item"),
                Arguments.of(new Date().getClass(), "Date")
        );
    }

    private static Stream<Arguments> objectToLocation() {
        return Stream.of(
                Arguments.of(List.of(), "ImmutableList"),
                Arguments.of(List.of("Item"), "ImmutableList<String>"),
                Arguments.of(new ArrayList<>(), "ArrayList"),
                Arguments.of(new HashMap<>(), "HashMap"),
                Arguments.of(new HashSet<>(), "HashSet"),
                Arguments.of(new int[1], "int[]"),
                Arguments.of("Text", "String"),
                Arguments.of((short) 10, "Short"),
                Arguments.of('a', "Character"),
                Arguments.of(MessageCreatorTest.Color.RED, "Color"),
                Arguments.of(new Item(), "Item"),
                Arguments.of(new Date(), "Date")
        );
    }

    private static Stream<Arguments> objectWithFieldToLocation() throws NoSuchFieldException {
        return Stream.of(
                Arguments.of(new Item(), Item.class.getDeclaredField("name"), "Item.name"),
                Arguments.of(new Item(), Item.class.getDeclaredField("namesList"), "Item.namesList()"),
                Arguments.of(new Item(), Item.class.getDeclaredField("namesMap"), "Item.namesMap()"),
                Arguments.of(new Item(), Item.class.getDeclaredField("numbers"), "Item.numbers[]")
        );
    }

    static class Item {
        String name = "Jane";
        List<String> namesList = List.of("Jussi", "Arthur");
        Map<String, String> namesMap = Map.of("Tom", "Manager");
        int[] numbers = {1};
    }
}
