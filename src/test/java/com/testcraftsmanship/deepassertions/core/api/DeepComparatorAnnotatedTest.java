package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
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
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeepComparatorAnnotatedTest extends BaseTest {
    private static Config config;

    @BeforeAll
    public static void setUp() {
        config = new Config();
        config.setWithAnyOrder(false);
    }

    @Test
    public void shouldUseEqualsOnObjectsWhenNoDeepVerifiableAnnotation() {
        config = new Config();
        config.setWithAnyOrder(false);
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        Mage expectedMage = new Mage("Dumbledore", 10,  new Staff(false, 5));
        LocationCreator locationCreator = new LocationCreator(config, Mage.class);
        DeepComparator deepComparator = new DeepComparator(config);
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
        LocationCreator locationCreator = new LocationCreator(config, Elf.class);
        DeepComparator deepComparator = new DeepComparator(config);
        assertThatFunctionThrows(() -> deepComparator.compare(actualElf, expectedElf, Elf.class, locationCreator),
                List.of("Multiple Failures (4 failures)",
                        "Elf.firstName<String> - actual object has value {Miirphys}, expect to have {Ailuin}",
                        "Elf.weapon.name<String> - actual object has value {Sword}, expect to have {Bow}",
                        "Elf.weapon.distance<Integer> - actual object has value {0}, expect to have {100}",
                        "Elf.weapon.material<Material> - actual object has value {Steel (10 kg)}, expect to have {Wood (2 kg)}"));
    }

    @Test
    public void classLevelAnnotationDeepVerifiableShouldWorkOnlyForFieldsOfTheClass() {
        ClassA expectedClassA = new ClassA();
        expectedClassA.value = "A expected";
        expectedClassA.classB.value = "expected";
        expectedClassA.classB.classC.value = "expected";
        ClassA actualClassA = new ClassA();
        actualClassA.value = "A actual";
        actualClassA.classB.value = "actual";
        actualClassA.classB.classC.value = "actual";
        LocationCreator locationCreator = new LocationCreator(config, expectedClassA.getClass());
        DeepComparator deepComparator = new DeepComparator(config);
        assertThatFunctionThrows(() -> deepComparator.compare(expectedClassA, actualClassA, expectedClassA.getClass(), locationCreator),
                List.of("Multiple Failures (2 failures)",
                        "ClassA.value<String> - actual object has value {A expected}, expect to have {A actual}",
                        "ClassA.classB.value<String> - actual object has value {expected}, expect to have {actual}"));
    }

}

@DeepVerifiable
class ClassA{
    public String value;
    public ClassB classB = new ClassB();
}

class ClassB {
    public String value;
    public ClassC classC = new ClassC();
}

class ClassC {
    @DeepVerifiable
    public String value;

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}