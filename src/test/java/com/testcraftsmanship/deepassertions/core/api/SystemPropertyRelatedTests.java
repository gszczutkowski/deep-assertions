package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.base.BaseTest;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Mage;
import com.testcraftsmanship.deepassertions.core.base.testclasses.local.Staff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class SystemPropertyRelatedTests extends BaseTest {

    @Test
    public void defaultPackageShouldBeTakenFromSystemPropertiesWithLocalAssertType() {
        System.setProperty("DEEP_ASSERT_PACKAGES", "com.testit");
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        Mage expectedMage = new Mage("Dumbledore", 10, new Staff(false, 5));
        DeepAssertions
                .assertThat(actualMage)
                .withPackages()
                .isEqualTo(expectedMage);
    }

    @Test
    public void defaultPackageShouldBeTakenFromSystemProperties() {
        System.setProperty("DEEP_ASSERT_PACKAGES", "com.testit");
        Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
        Mage expectedMage = new Mage("Dumbledore", 10, new Staff(false, 5));
        DeepAssertions
                .assertThat(actualMage)
                .isEqualTo(expectedMage);
    }

    @AfterEach
    public void cleanUp() {
        System.clearProperty("DEEP_ASSERT_PACKAGES");
    }
}
