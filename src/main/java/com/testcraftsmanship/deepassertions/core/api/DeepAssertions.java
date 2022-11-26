package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.api.comparator.AnnotatedDeepComparator;
import com.testcraftsmanship.deepassertions.core.api.comparator.DeepComparator;
import com.testcraftsmanship.deepassertions.core.api.comparator.LocalDeepComparator;
import com.testcraftsmanship.deepassertions.core.api.items.DeepAssertType;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DeepAssertions {
    private final Object actualItem;
    private final Config config;

    private DeepAssertions(Object actualItem) {
        this.actualItem = actualItem;
        this.config = new Config();
    }

    public static DeepAssertions assertThat(Object actual) {
        return new DeepAssertions(actual);
    }

    /**
     * Method triggers comparison of expected object passed as argument with object passed in assertThat method.
     * Type a parameters of comparison can be set with different methods available in this class.
     * @param expected object
     */
    public void isEqualTo(Object expected) {
        getComparator().compare(actualItem, expected, expected.getClass(), new LocationCreator(actualItem.getClass()));
    }

    /**
     * This method sets the flag which forces arrays or collections to be compared in any order. It means that
     * e.g. array {"Tom", "Jerry"} is equal to {"Jerry", "Tom"} when this flag is set.
     * @return DeepAssertion instance
     */
    public DeepAssertions withAnyOrder() {
        config.setWithAnyOrder(true);
        return this;
    }

    /**
     * This method sets the list of packages from which classes can be deeply verifiable. It is for preventing deep
     * verification of e.g. fields with String type. If we want to deeply verify String objects then we need to add
     * java.lang package with this method.
     * Method overrides default value or value set by system properties.
     * @param packages to be taken into consideration in deep assertion
     * @return DeepAssertion instance
     */
    public DeepAssertions withPackages(String... packages) {
        config.setDeepVerifiablePackages(packages);
        return this;
    }

    /**
     * This method sets the flag that all fields from the object of the class that is compared are not taken into
     * consideration while comparing. It means that if you want to compare any field then you need to annotate it
     * with @Verifiable annotation.
     * @return DeepAssertion instance
     */
    public DeepAssertions withExcludingAllFieldsByDefault() {
        config.excludeAllFieldsByDefault();
        return this;
    }

    /**
     * This method sets annotation include tags. When @Verifiable annotation contains at least one of the tags passed with
     * this method then the field will be taken into consideration in comparison.
     * @param tags at least of them have to be included in @Verifiable annotation to perform comparison on the field
     * @return DeepAssertion instance
     */
    public DeepAssertions withAssertionIncludeTags(String... tags) {
        config.setAssertIncludedTags(tags);
        return this;
    }

    private DeepComparator getComparator() {
        if (DeepAssertType.LOCAL.equals(config.getDeepAssertType())) {
            return new LocalDeepComparator(config);
        } else if (DeepAssertType.ANNOTATED.equals(config.getDeepAssertType())) {
            return new AnnotatedDeepComparator(config);
        } else {
            throw new IllegalStateException("There is no matching comparator");
        }
    }
}
