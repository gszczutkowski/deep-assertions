package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.api.comparator.DeepComparator;
import com.testcraftsmanship.deepassertions.core.api.comparator.LocalDeepComparator;
import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.Elf;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.config.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DeepComparatorLocalTest extends BaseTest {



    @Test
    public void excludeAnnotationShouldWorkWithAnnotatedType() throws NoSuchFieldException {
        Config config = new Config();
        DeepComparator deepComparator = new LocalDeepComparator(config);

        Field matchingPackageFieldIncluded = ClassLocalA.class.getDeclaredField("includedObjectB");
        boolean isIncludedDeepVerifiable = deepComparator.isDeepVerifiableField(ClassLocalA.class, matchingPackageFieldIncluded);
        org.assertj.core.api.Assertions.assertThat(isIncludedDeepVerifiable).isTrue();

        Field matchingPackageFieldExcluded = ClassLocalA.class.getDeclaredField("excludedObjectB");
        boolean isExcludedDeepVerifiable = deepComparator.isDeepVerifiableField(ClassLocalA.class, matchingPackageFieldExcluded);
        org.assertj.core.api.Assertions.assertThat(isExcludedDeepVerifiable).isFalse();
    }

    private static Stream<Arguments> objectWithFailuresMessage() {
        return Stream.of(
                Arguments.of(new Elf("Ascal", "Holana"), new Elf("Arlen", "Holana"),
                        List.of("Multiple Failures (1 failure)",
                                String.format(Messages.DIFFERENT_VALUES, "Elf.firstName", "String", "Ascal", "Arlen"))),
                Arguments.of(List.of(new Elf("Raibyn", "Caimaris")), List.of(new Elf("Aymar", "Heleric")),
                        List.of("Multiple Failures (2 failures)",
                                String.format(Messages.DIFFERENT_VALUES, "ImmutableList(0).lastName", "String", "Caimaris", "Heleric"),
                                String.format(Messages.DIFFERENT_VALUES, "ImmutableList(0).firstName", "String", "Raibyn", "Aymar"))),
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

class ClassLocalA {
    public String value;
    @DeepVerifiableExclude
    public ClassLocalB excludedObjectB;
    public ClassLocalB includedObjectB;
}

class ClassLocalB {
}
