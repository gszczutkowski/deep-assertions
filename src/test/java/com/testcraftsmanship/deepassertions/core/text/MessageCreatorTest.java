package com.testcraftsmanship.deepassertions.core.text;

import com.testcraftsmanship.deepassertions.core.config.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.testcraftsmanship.deepassertions.core.text.MessageCreatorTest.Color.BLACK;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreatorTest.Color.RED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageCreatorTest {

    @ParameterizedTest
    @MethodSource("objectToMessage")
    public void correctMessageShouldBeCreatedForObjects(Object actual, Object expected, String expectedMessage) {
        String path = "Location.item";
        UpdateInfo updateInfo = new UpdateInfo(actual.getClass());
        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path, updateInfo);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("objectToMessageInvalid")
    public void correctMessageShouldBeCreatedForInvalidObjects(Object actual, Object expected, String expectedMessage) {
        String path = "Location.item";
        Class theClass = actual != null ? actual.getClass() : expected.getClass();
        UpdateInfo updateInfo = new UpdateInfo(theClass);
        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path, updateInfo);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("objectSizeToMessage")
    public void correctMessageShouldBeCreatedForSizes(int actual, int expected, Object object, String expectedMessage) {
        String path = "Location.item";
        UpdateInfo updateInfo = new UpdateInfo(object.getClass());
        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path, updateInfo);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void exceptionShouldBeThrownWhenBothObjectsAreNull() {
        String path = "Location.item";
        assertThatThrownBy(() -> {
            MessageCreator.failMessageCreator(null, null, path, new UpdateInfo(null));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll(
                        "Failure message can not be generated as both objects are null");
    }

    @Test
    public void exceptionShouldBeThrownWhenObjectIsNull() {
        String path = "Location.item";

        assertThatThrownBy(() -> {
            MessageCreator.failMessageCreator(null, true, path);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll(
                        "Failure message can not be generated as passed object is null");
    }




    private static Stream<Arguments> objectToMessage() {
        return Stream.of(
                Arguments.of(true, false, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Boolean", "true", "false")),
                Arguments.of(100, -100, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Integer", "100", "-100")),
                Arguments.of((short) 10, (short)1, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Short", "10", "1")),
                Arguments.of(0.025D, 0.02D, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Double", "0.025", "0.02")),
                Arguments.of(0.025F, 0.02F, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Float", "0.025", "0.02")),
                Arguments.of(1000L, 0L, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Long", "1000", "0")),
                Arguments.of('a', 'b', String.format(Messages.DIFFERENT_VALUES, "Location.item", "Character", "a", "b")),
                Arguments.of("Adam", "Ada", String.format(Messages.DIFFERENT_VALUES, "Location.item", "String", "Adam", "Ada")),
                Arguments.of(RED, BLACK, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Color", "RED", "BLACK")),
                Arguments.of(LocalDateTime.of(2020,10,5,12, 30, 10),
                        LocalDateTime.of(2020,10,5,0, 30, 10),
                        String.format(Messages.DIFFERENT_VALUES, "Location.item", "LocalDateTime", "2020-10-05T12:30:10", "2020-10-05T00:30:10"))
        );
    }

    private static Stream<Arguments> objectToMessageInvalid() {
        return Stream.of(
                Arguments.of(null, false, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Boolean", "null", "false")),
                Arguments.of(100, null, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Integer", "100", "null")),
                Arguments.of((short) 10, (long)1, String.format(Messages.DIFFERENT_TYPES, "Location.item", "Short", "Long")),
                Arguments.of('a', "b", String.format(Messages.DIFFERENT_TYPES, "Location.item", "Character", "String")),
                Arguments.of(100, "Ada", String.format(Messages.DIFFERENT_TYPES, "Location.item", "Integer", "String")),
                Arguments.of(RED, null, String.format(Messages.DIFFERENT_VALUES, "Location.item", "Color", "RED", "null")),
                Arguments.of(RED, "Adam", String.format(Messages.DIFFERENT_TYPES, "Location.item", "Color", "String"))
        );
    }

    private static Stream<Arguments> objectSizeToMessage() {
        return Stream.of(
                Arguments.of(0, 1, new int[1], String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "int[]", "are 0 items", "1")),
                Arguments.of(0, 10000, new ArrayList<>(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "ArrayList", "are 0 items", "10000")),
                Arguments.of(0, 10, new LinkedList<>(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "LinkedList", "are 0 items", "10")),
                Arguments.of(999999999, 1, new HashSet<>(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "HashSet", "are 999999999 items", "1")),
                Arguments.of(0, 1, new HashMap<>(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "HashMap", "are 0 items", "1")),
                Arguments.of(0, 1, new Stack<>(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "Stack", "are 0 items", "1")),
                Arguments.of(0, 1, List.of(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "ImmutableList", "are 0 items", "1")),
                Arguments.of(0, 1, Set.of(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "ImmutableSet", "are 0 items", "1")),
                Arguments.of(0, 1, Map.of(), String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "Location.item", "ImmutableMap", "are 0 items", "1"))
        );
    }

    enum Color {
        RED, BLACK;
    }
}
