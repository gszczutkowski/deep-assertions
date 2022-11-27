package com.testcraftsmanship.deepassertions.core.config;

import com.testcraftsmanship.deepassertions.core.api.items.DeepAssertType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class ConfigTest {
    @Test
    public void defaultPackageAndAssertTypeShouldBeTakenFromSystemProperties() {
        System.setProperty("DEEP_ASSERT_TYPE", "local");
        System.setProperty("DEEP_ASSERT_PACKAGES", "pl.testit, com.testcraftsmanship");

        Config config = new Config();

        assertThat(config.getDeepAssertType()).isEqualTo(DeepAssertType.LOCAL);
        assertThat(config.getDeepVerifiablePackages()).contains("pl.testit", "com.testcraftsmanship");
    }

    @Test
    public void defaultPackageShouldNotBeTakenFromSystemPropertiesWhenEmpty() {
        System.setProperty("DEEP_ASSERT_PACKAGES", "");
        Config config = new Config();

        assertThat(config.getDeepAssertType()).isEqualTo(DeepAssertType.ANNOTATED);
        assertThat(config.getDeepVerifiablePackages()).contains("com.testcraftsmanship");
    }

    @Test
    public void defaultPackageShouldNotBeTakenFromSystemPropertiesWhenInvalidDomain() {
        System.setProperty("DEEP_ASSERT_PACKAGES", "pl-wrongdomain");
        Config config = new Config();

        assertThat(config.getDeepAssertType()).isEqualTo(DeepAssertType.ANNOTATED);
        assertThat(config.getDeepVerifiablePackages()).contains("com.testcraftsmanship");
    }

    @Test
    public void deepAssertTypeShouldBeTakenFromSystemProperties() {
        System.setProperty("DEEP_ASSERT_TYPE", "loCal");
        Config config = new Config();

        assertThat(config.getDeepAssertType()).isEqualTo(DeepAssertType.LOCAL);
    }

    @Test
    public void deepAssertTypeShouldNotBeTakenFromSystemPropertiesWhenEmpty() {
        System.setProperty("DEEP_ASSERT_TYPE", " ");
        Config config = new Config();

        assertThat(config.getDeepAssertType()).isEqualTo(DeepAssertType.ANNOTATED);
    }

    @Test
    public void assertTagsShouldBeTakenFromSystemProperties() {
        System.setProperty("ASSERT_TAGS", "UI, Rest-API, old");
        Config config = new Config();

        assertThat(config.getAssertIncludeTags()).contains("UI", "Rest-API", "old");
    }

    @Test
    public void assertTagsShouldNotBeTakenFromSystemPropertiesWhenEmpty() {
        System.setProperty("ASSERT_TAGS", "");
        Config config = new Config();

        assertThat(config.getAssertIncludeTags()).isEmpty();
    }

    @Test
    public void assertTagsShouldNotBeTakenFromSystemPropertiesWhenInvalid() {
        System.setProperty("ASSERT_TAGS", "pl-'wrongdomain?");
        Config config = new Config();

        assertThat(config.getAssertIncludeTags()).isEmpty();
    }

    @AfterEach
    public void cleanUp() {
        System.clearProperty("DEEP_ASSERT_PACKAGES");
        System.clearProperty("DEEP_ASSERT_TYPE");
        System.clearProperty("ASSERT_TAGS");
    }
}
