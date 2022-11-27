package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.api.comparator.AnnotatedDeepComparator;
import com.testcraftsmanship.deepassertions.core.api.comparator.DeepComparator;
import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.Elf;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.ElfWarrior;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.Material;
import com.testcraftsmanship.deepassertions.core.base.testclasses.annotated.Weapon;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Mage;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Staff;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

public class DeepComparatorAnnotatedTest extends BaseTest {
    private static Config config;
    private DeepComparator deepComparator;

    @BeforeAll
    public static void setUp() {
        config = new Config();
        config.setWithAnyOrder(false);
    }

    @BeforeEach
    public void testSetUp() {
        deepComparator = new AnnotatedDeepComparator(config);
    }

    @Test
    public void shouldUseEqualsOnObjectsWhenNoDeepVerifiableAnnotation() {
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        Mage expectedMage = new Mage("Dumbledore", 10,  new Staff(false, 5));
        LocationCreator locationCreator = new LocationCreator(Mage.class);
        assertThatFunctionThrows(() -> deepComparator.compare(actualMage, expectedMage, Mage.class, locationCreator),
                List.of("Multiple Failures (1 failure)",
                        "Mage.name<String> - actual object has value {Gandalf}, expect to have {Dumbledore}"));
    }

    @Test
    public void onlyMarkedAsDeepVerifiableShouldBeDeeplyVerified() {
        ElfWarrior actualElf = new ElfWarrior("Miirphys", "Keanan",
                new Weapon("Sword", 0, 5, new Material("Steel", 10)));
        ElfWarrior expectedElf = new ElfWarrior("Ailuin", "Keanan",
                new Weapon("Bow", 100, 5, new Material("Wood", 2)));
        LocationCreator locationCreator = new LocationCreator(Elf.class);
        assertThatFunctionThrows(() -> deepComparator.compare(actualElf, expectedElf, Elf.class, locationCreator),
                List.of("Multiple Failures (4 failures)",
                        "Elf.firstName<String> - actual object has value {Miirphys}, expect to have {Ailuin}",
                        "Elf.weapon.name<String> - actual object has value {Sword}, expect to have {Bow}",
                        "Elf.weapon.distance<Integer> - actual object has value {0}, expect to have {100}",
                        "Elf.weapon.material<Material> - actual object has value {Steel (10 kg)}, expect to have {Wood (2 kg)}"));
    }

    @Test
    public void classLevelAnnotationDeepVerifiableShouldWorkOnlyForFieldsOfTheClass() {
        ClassA1 expectedClassA = new ClassA1();
        expectedClassA.value = "A expected";
        expectedClassA.classB.value = "expected";
        expectedClassA.classB.classC.value = "expected";
        ClassA1 actualClassA = new ClassA1();
        actualClassA.value = "A actual";
        actualClassA.classB.value = "actual";
        actualClassA.classB.classC.value = "actual";
        LocationCreator locationCreator = new LocationCreator(expectedClassA.getClass());
        assertThatFunctionThrows(() -> deepComparator.compare(expectedClassA, actualClassA, expectedClassA.getClass(), locationCreator),
                List.of("Multiple Failures (2 failures)",
                        "ClassA1.value<String> - actual object has value {A expected}, expect to have {A actual}",
                        "ClassA1.classB.value<String> - actual object has value {expected}, expect to have {actual}"));
    }

    @Test
    public void fieldAnnotatedOnClassLevelShouldBeVerifiable() throws NoSuchFieldException {
        Field annotatedOnFieldLevel = ClassA3.class.getDeclaredField("fieldA");
        boolean isDeepVerifiableField = deepComparator.isDeepVerifiableField(ClassA3.class, annotatedOnFieldLevel);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableField).isTrue();

    }

    @Test
    public void fieldAnnotatedOnFieldLevelShouldBeVerifiable() throws NoSuchFieldException {
        Field annotatedOnClassLevel = ClassA2.class.getDeclaredField("fieldA");
        boolean isDeepVerifiableField = deepComparator.isDeepVerifiableField(ClassA2.class, annotatedOnClassLevel);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableField).isTrue();
    }

    @Test
    public void fieldNotAnnotatedShouldNotBeVerifiable() throws NoSuchFieldException {
        Field notAnnotatedOnField = ClassA3.class.getDeclaredField("fieldB");
        boolean isDeepVerifiableField = deepComparator.isDeepVerifiableField(ClassA3.class, notAnnotatedOnField);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableField).isFalse();
    }

    @Test
    public void fieldAnnotatedWithExcludeShouldNotBeVerifiable() throws NoSuchFieldException {
        Field annotatedWitExclude = ClassA2.class.getDeclaredField("fieldB");
        boolean isDeepVerifiableField = deepComparator.isDeepVerifiableField(ClassA3.class, annotatedWitExclude);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableField).isFalse();
    }

    @Test
    public void fieldDoubleAnnotatedShouldBeVerifiable() throws NoSuchFieldException {
        Field annotatedOnTweLevels = ClassA2.class.getDeclaredField("fieldC");
        boolean isDeepVerifiableField = deepComparator.isDeepVerifiableField(ClassA2.class, annotatedOnTweLevels);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableField).isTrue();
    }

}

@DeepVerifiable
class ClassA1 {
    public String value;
    public ClassB1 classB = new ClassB1();
}

class ClassB1 {
    public String value;
    public ClassC1 classC = new ClassC1();
}

class ClassC1 {
    @DeepVerifiable
    public String value;

    @Override
    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}

@DeepVerifiable
class ClassA2 {
    private String fieldA;
    @DeepVerifiableExclude
    private String fieldB;
    @DeepVerifiable
    private String fieldC;
}

class ClassA3 {
    @DeepVerifiable
    private String fieldA;
    private String fieldB;
}
