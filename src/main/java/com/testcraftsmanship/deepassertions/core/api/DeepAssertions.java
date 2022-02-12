package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class DeepAssertions {
    private List<String> deepAssertTags;
    private DeepComparator deepComparator;
    private boolean withAnyOrder = false;
    private Object actualItem;

    private DeepAssertions(Object actualItem) {
        this.deepAssertTags = new ArrayList<>();
        this.deepAssertTags.add("item");
        this.actualItem = actualItem;
    }

    public static DeepAssertions assertThat(Object actual) {
        return new DeepAssertions(actual);
    }

    public void isEqualTo(Object expected) {
        this.deepComparator = new DeepComparator(withAnyOrder);
        deepComparator.compare(actualItem, expected, new LocationCreator(actualItem.getClass()));
    }

    public DeepAssertions withAnyOrder() {
        withAnyOrder = true;
        return this;
    }

}
