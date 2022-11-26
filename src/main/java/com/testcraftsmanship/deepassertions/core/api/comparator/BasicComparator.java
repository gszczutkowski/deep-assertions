package com.testcraftsmanship.deepassertions.core.api.comparator;

import com.testcraftsmanship.deepassertions.core.assertions.AssertionCreator;
import com.testcraftsmanship.deepassertions.core.text.ActualObjectState;

import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.failMessageCreator;

public final class BasicComparator {
    private BasicComparator() {
    }

    public static void basicCompare(Object actualItem, Object expectedItem, String location,
                                    ActualObjectState actualObjectState, AssertionCreator assertionCreator) {
        if (!actualItem.equals(expectedItem)) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem,
                    location, actualObjectState));
        }
    }
}
