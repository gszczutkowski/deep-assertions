package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DeepAssertions {
    private final Object actualItem;
    private DeepComparator deepComparator;
    private Config config;

    private DeepAssertions(Object actualItem) {
        this.actualItem = actualItem;
        this.config = new Config(DeepAssertType.LOCAL);
    }

    public static DeepAssertions assertThat(Object actual) {
        return new DeepAssertions(actual);
    }

    public void isEqualTo(Object expected) {
        this.deepComparator = new DeepComparator(config);
        deepComparator.compare(actualItem, expected, new LocationCreator(config, actualItem.getClass()));
    }

    public DeepAssertions withAnyOrder() {
        config.setWithAnyOrder(true);
        return this;
    }

    public DeepAssertions withPackages(String... packages) {
        config.setDeepVerifiablePackages(packages);
        return this;
    }
}
