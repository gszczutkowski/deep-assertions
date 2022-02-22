package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.extern.slf4j.Slf4j;

import static com.testcraftsmanship.deepassertions.core.config.Config.DEFAULT_DEEP_ASSERT_TYPE;

@Slf4j
public final class DeepAssertions {
    private final Object actualItem;
    private DeepComparator deepComparator;
    private Config config;

    private DeepAssertions(Object actualItem) {
        this.actualItem = actualItem;
        this.config = new Config();
    }

    public static DeepAssertions assertThat(Object actual) {
        return new DeepAssertions(actual);
    }

    public void isEqualTo(Object expected) {
        this.deepComparator = getComparator(config);
        deepComparator.compare(actualItem, expected, expected.getClass(), new LocationCreator(config, actualItem.getClass()));
    }

    public DeepAssertions withAnyOrder() {
        config.setWithAnyOrder(true);
        return this;
    }

    public DeepAssertions withPackages(String... packages) {
        if (DEFAULT_DEEP_ASSERT_TYPE.equals(config.getDeepAssertType())) {
            config.setDeepVerifiablePackages(packages);
            return this;
        }
        throw new IllegalStateException("You can use default assertion type or set one of: LOCAL or DEFINED");
    }

    public DeepAssertions withAnnotationTags(String... tags) {
        if (DEFAULT_DEEP_ASSERT_TYPE.equals(config.getDeepAssertType())) {
            config.setDeepAssertTags(tags);
            return this;
        }
        throw new IllegalStateException("You can use default assertion type or set one of: LOCAL or ANNOTATED");
    }

    private DeepComparator getComparator(Config config) {
        if (DeepAssertType.LOCAL.equals(config.getDeepAssertType())) {
            return new LocalDeepComparator(config);
        } else if (DeepAssertType.ANNOTATED.equals(config.getDeepAssertType())) {
            return new AnnotatedDeepComparator(config);
        } else if (DeepAssertType.DEFINED.equals(config.getDeepAssertType())) {
            return new DefinedDeepComparator(config);
        } else {
            throw new IllegalStateException("There is no matching comparator");
        }
    }
}
