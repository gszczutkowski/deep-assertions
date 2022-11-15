package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.api.comparator.DeepComparator;
import com.testcraftsmanship.deepassertions.core.api.comparator.DefinedDeepComparator;
import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeepComparatorDefinedTest extends BaseTest {

    @Test
    public void shouldUseEqualsOnObjectsWhenNoDefinedDeepVerifiableAnnotationTag() {
        Config config = new Config();
        config.setDeepAssertTags("rest");
        Parent expectedParent = new Parent(new ChildA("Jon"), new ChildB("Lily"));
        Parent actualParent = new Parent(new ChildA("Tom"), new ChildB("Max"));
        LocationCreator locationCreator = new LocationCreator(Parent.class);
        DeepComparator deepComparator = new DefinedDeepComparator(config);

        assertThatFunctionThrows(() -> deepComparator.compare(actualParent, expectedParent, Parent.class, locationCreator),
                List.of("Multiple Failures (2 failures)",
                        "Parent.childA.name<String> - actual object has value {Tom}, expect to have {Jon}",
                        "Parent.childB<ChildB> - actual object has value {ChildB(name=Max)}, expect to have {ChildB(name=Lily)}"));
    }

    @Test
    public void shouldUseDeepVerificationOnObjectsWithDefinedDeepVerifiableAnnotationTag() {
        Config config = new Config();
        config.setDeepAssertTags("gui");
        Parent expectedParent = new Parent(new ChildA("Jon"), new ChildB("Lily"));
        Parent actualParent = new Parent(new ChildA("Tom"), new ChildB("Max"));
        LocationCreator locationCreator = new LocationCreator(Parent.class);
        DeepComparator deepComparator = new DefinedDeepComparator(config);

        assertThatFunctionThrows(() -> deepComparator.compare(actualParent, expectedParent, Parent.class, locationCreator),
                List.of("Multiple Failures (2 failures)",
                        "Parent.childA.name<String> - actual object has value {Tom}, expect to have {Jon}",
                        "Parent.childB.name<String> - actual object has value {Max}, expect to have {Lily}"));
    }

    @Test
    public void shouldUseDeepVerificationOnObjectsWithDefinedDeepVerifiableAnnotationTagOnClassLevel() {
        Config config = new Config();
        config.setDeepAssertTags("gui");
        Grandpa expectedGrandpa = new Grandpa(new Parent(new ChildA("Jon"), new ChildB("Lily")));
        Grandpa actualGrandpa = new Grandpa(new Parent(new ChildA("Tom"), new ChildB("Lily")));
        LocationCreator locationCreator = new LocationCreator(Grandpa.class);
        DeepComparator deepComparator = new DefinedDeepComparator(config);

        assertThatFunctionThrows(() -> deepComparator.compare(actualGrandpa, expectedGrandpa, Grandpa.class, locationCreator),
                List.of("Multiple Failures (1 failure)",
                        "Grandpa.parent.childA.name<String> - actual object has value {Tom}, expect to have {Jon}"));
    }
}

@DeepVerifiable(tags = {"rest", "gui"})
@AllArgsConstructor
class Grandpa {
    public Parent parent;
}

@AllArgsConstructor
class Parent {
    @DeepVerifiable(tags = {"rest", "gui"})
    public ChildA childA;
    @DeepVerifiable(tags = "gui")
    public  ChildB childB;
}

@ToString
@AllArgsConstructor
@EqualsAndHashCode
class ChildA {
    public String name;
}

@ToString
@AllArgsConstructor
@EqualsAndHashCode
class  ChildB {
    public String name;
}
