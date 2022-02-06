package com.testcraftsmanship.core.text;

import com.testcraftsmanship.deepassertions.core.text.MessageCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.testcraftsmanship.core.text.MessageCreatorTest.Color.BLACK;
import static com.testcraftsmanship.core.text.MessageCreatorTest.Color.RED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageCreatorTest {

    @ParameterizedTest
    @MethodSource("objectToMessage")
    public void correctMessageShouldBeCreatedForObjects(Object actual, Object expected, String expectedMessage) {
        String path = "Location.item";

        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("objectToMessageInvalid")
    public void correctMessageShouldBeCreatedForInvalidObjects(Object actual, Object expected, String expectedMessage) {
        String path = "Location.item";

        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("objectSizeToMessage")
    public void correctMessageShouldBeCreatedForSizes(int actual, int expected, Object object, String expectedMessage) {
        String path = "Location.item";

        String actualMessage = MessageCreator.failMessageCreator(actual, expected, path, object.getClass());
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void exceptionShouldBeThrownWhenBothObjectsAreNull() {
        String path = "Location.item";

        assertThatThrownBy(() -> {
            MessageCreator.failMessageCreator(null, null, path);
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
                Arguments.of(true, false, "Location.item<Boolean> - actual object has value true, expect to have false"),
                Arguments.of(100, -100, "Location.item<Integer> - actual object has value 100, expect to have -100"),
                Arguments.of((short) 10, (short)1, "Location.item<Short> - actual object has value 10, expect to have 1"),
                Arguments.of(0.025D, 0.02D, "Location.item<Double> - actual object has value 0.025, expect to have 0.02"),
                Arguments.of(0.025F, 0.02F, "Location.item<Float> - actual object has value 0.025, expect to have 0.02"),
                Arguments.of(1000L, 0L, "Location.item<Long> - actual object has value 1000, expect to have 0"),
                Arguments.of('a', 'b', "Location.item<Character> - actual object has value a, expect to have b"),
                Arguments.of("Adam", "Ada", "Location.item<String> - actual object has value Adam, expect to have Ada"),
                Arguments.of(RED, BLACK, "Location.item<Color> - actual object has value RED, expect to have BLACK"),
                Arguments.of(LocalDateTime.of(2020,10,5,12, 30, 10),
                        LocalDateTime.of(2020,10,5,0, 30, 10),
                        "Location.item<LocalDateTime> - actual object has value 2020-10-05T12:30:10, expect to have 2020-10-05T00:30:10")
        );
    }

    private static Stream<Arguments> objectToMessageInvalid() {
        return Stream.of(
                Arguments.of(null, false, "Location.item<Boolean> - actual object is null but expected is false"),
                Arguments.of(100, null, "Location.item<Integer> - actual object is 100 but expected is null"),
                Arguments.of((short) 10, (long)1, "Location.item - actual object type is Short but expected object type is Long"),
                Arguments.of('a', "b", "Location.item - actual object type is Character but expected object type is String"),
                Arguments.of(100, "Ada", "Location.item - actual object type is Integer but expected object type is String"),
                Arguments.of(RED, null, "Location.item<Color> - actual object is RED but expected is null"),
                Arguments.of(RED, "Adam", "Location.item - actual object type is Color but expected object type is String")
        );
    }

    private static Stream<Arguments> objectSizeToMessage() {
        return Stream.of(
                Arguments.of(0, 1, new int[1], "Location.item<int[]> - actual object has size 0 but expected to have size 1"),
                Arguments.of(0, 10000, new ArrayList<>(), "Location.item<ArrayList> - actual object has size 0 but expected to have size 10000"),
                Arguments.of(0, 10, new LinkedList<>(), "Location.item<LinkedList> - actual object has size 0 but expected to have size 10"),
                Arguments.of(999999999, 1, new HashSet<>(), "Location.item<HashSet> - actual object has size 999999999 but expected to have size 1"),
                Arguments.of(0, 1, new HashMap<>(), "Location.item<HashMap> - actual object has size 0 but expected to have size 1"),
                Arguments.of(0, 1, new Stack<>(), "Location.item<Stack> - actual object has size 0 but expected to have size 1"),
                Arguments.of(0, 1, List.of(), "Location.item<ImmutableList> - actual object has size 0 but expected to have size 1"),
                Arguments.of(0, 1, Set.of(), "Location.item<ImmutableSet> - actual object has size 0 but expected to have size 1"),
                Arguments.of(0, 1, Map.of(), "Location.item<ImmutableMap> - actual object has size 0 but expected to have size 1")
        );
    }

    enum Color {
        RED, BLACK;
    }
}
