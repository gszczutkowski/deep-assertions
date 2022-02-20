package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.Elf;
import com.testcraftsmanship.deepassertions.core.config.Messages;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DeepAssertionsTest extends BaseTest {
    private static Elf first = new Elf("Lianthorn", "Ermyar");
    private static Elf second = new Elf("Rennyn", "Rosalor");

    @ParameterizedTest
    @MethodSource("sameCollectionsWithDifferentOrder")
    public void itShouldBePossibleToIgnoreOrderOfCollections(Object actual, Object expected) {
        DeepAssertions.assertThat(actual)
                .withAnyOrder()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("differentCollectionsWithDifferentOrder")
    public void itShouldThrowErrorWhenDifferentCollectionsWithIgnoredOrder(Object actual, Object expected, List<String> expectedMessages) {
        assertThatFunctionThrows(() -> DeepAssertions.assertThat(actual)
                .withAnyOrder()
                .isEqualTo(expected), expectedMessages);
    }

    private static Stream<Arguments> sameCollectionsWithDifferentOrder() {
        return Stream.of(
                Arguments.of(List.of(first, first, second), List.of(second, first, first)),
                Arguments.of(Set.of(first, second), Set.of(second, first)),
                Arguments.of(new String[]{"Steamstrand", "Winterhorn", "Hazelbreak"}, new String[]{"Winterhorn", "Steamstrand", "Hazelbreak"}),
                Arguments.of(new String[]{"Steamstrand", "Steamstrand", "Hazelbreak"}, new String[]{"Hazelbreak", "Steamstrand", "Steamstrand"}),
                Arguments.of(Map.of("city", "Fallwallow", "king", "Jonik Elamaris"), Map.of("king", "Jonik Elamaris", "city", "Fallwallow"))
        );
    }

    private static Stream<Arguments> differentCollectionsWithDifferentOrder() {
        return Stream.of(
                Arguments.of(List.of(first, second, second), List.of(second, first, first),
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "ImmutableList(Lianthorn Ermyar)", "<Elf>", "is 1 item", "Lianthorn Ermyar", "2"),
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "ImmutableList(Rennyn Rosalor)", "<Elf>", "are 2 items", "Rennyn Rosalor", "1"))),
                Arguments.of(new String[]{"Steamstrand", "Winterhorn", "Winterhorn"}, new String[]{"Winterhorn", "Steamstrand", "Hazelbreak"},
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Winterhorn]", "", "are 2 items", "Winterhorn", "1"),
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Hazelbreak]", "", "are no items", "Hazelbreak", "1"))),
                Arguments.of(new String[]{"Steamstrand", "Winterhorn", "Hazelbreak"}, new String[]{"Winterhorn", "Hazelbreak", "Hazelbreak"},
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Hazelbreak]", "", "is 1 item", "Hazelbreak", "2"),
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Steamstrand]", "", "is 1 item", "Steamstrand", "0"))),
                Arguments.of(new String[]{"Steamstrand", "Steamstrand", "Hazelbreak"}, new String[]{"Hazelbreak", "Steamstrand"},
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "String[]", "String[]", "are 3 items", "2"),
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Steamstrand]", "", "are 2 items", "Steamstrand", "1"))),
                Arguments.of(new String[]{"Steamstrand", "Hazelbreak"}, new String[]{"Steamstrand", "Hazelbreak", "Steamstrand"},
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_COLLECTIONS_SIZES, "String[]", "String[]", "are 2 items", "3"),
                                String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE, "String[Steamstrand]", "", "is 1 item", "Steamstrand", "2"))),
                Arguments.of(Map.of("city", "Jonik Elamaris", "king", "Fallwallow"), Map.of("king", "Jonik Elamaris", "city", "Fallwallow"),
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_VALUES, "ImmutableMap(king)", "String", "Fallwallow", "Jonik Elamaris"),
                                String.format(Messages.DIFFERENT_VALUES, "ImmutableMap(city)", "String", "Jonik Elamaris", "Fallwallow")))
        );
    }
}
