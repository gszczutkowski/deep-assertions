package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Mage;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Staff;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeepComparatorTest  extends BaseTest {

    @Test
    public void nullAsAnExpectedItemShouldBeHandledCorrectly() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        DeepComparator deepComparator = new LocalDeepComparator(config);
        Staff staff = new Staff(true, 5);
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        LocationCreator locationCreator = new LocationCreator(Mage.class)
                .locationOfField(actualMage.getClass().getDeclaredField("staff"));

        assertThatFunctionThrows(() -> deepComparator.compare(staff, null, Mage.class, locationCreator),
                List.of("Multiple Failures (1 failure)",
                        "Mage.staff<Staff> - actual object has value {Two-handed staff with power 5}, expect to have {null}"));
    }

    @Test
    public void nullAsAnActualItemShouldBeHandledCorrectly() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        DeepComparator deepComparator = new LocalDeepComparator(config);
        Staff staff = new Staff(true, 5);
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        LocationCreator locationCreator = new LocationCreator(Mage.class)
                .locationOfField(actualMage.getClass().getDeclaredField("staff"));

        assertThatFunctionThrows(() -> deepComparator.compare(null, staff, Mage.class, locationCreator),
                List.of("Multiple Failures (1 failure)",
                        "Mage.staff<Staff> - actual object has value {null}, expect to have {Two-handed staff with power 5}"));
    }

    @Test
    public void differentTypesItemsShouldBeHandledCorrectly() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        DeepComparator deepComparator = new LocalDeepComparator(config);
        Staff staff = new Staff(true, 5);
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        LocationCreator locationCreator = new LocationCreator(Mage.class).locationOfField(actualMage.getClass().getDeclaredField("staff"));

        assertThatFunctionThrows(() -> deepComparator.compare(actualMage, staff, Mage.class, locationCreator),
                List.of("Multiple Failures (1 failure)",
                        "Mage.staff - actual object type is <Mage>, expected type is <Staff>"));
    }

    @Test
    public void bothNullsShouldBeHandledCorrectly() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        DeepComparator deepComparator = new LocalDeepComparator(config);
        Staff staff = new Staff(true, 5);
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        LocationCreator locationCreator = new LocationCreator(Mage.class).locationOfField(actualMage.getClass().getDeclaredField("staff"));

        deepComparator.compare(null, null, Mage.class, locationCreator);
    }

}
