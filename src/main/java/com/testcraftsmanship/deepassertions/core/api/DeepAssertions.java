package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.assertions.AssertionCreator;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableForType;
import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableOnClassLevel;
import static com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor.extractFieldType;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.failMessageCreator;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.variableInfo;

@Slf4j
public class DeepAssertions {
    private AssertionCreator assertionCreator = new AssertionCreator();
    private Config config = new Config(DeepAssertType.LOCAL);
    private List<String> deepAssertTags = new ArrayList<>();

    public DeepAssertions() {
        deepAssertTags.add("item");
    }

    public void assertEquals(Object actualItem, Object expectedItem) {
        assertionCreator = new AssertionCreator();
        deepCompare(actualItem, expectedItem, new LocationCreator(actualItem.getClass()));
        assertionCreator.performAssertions();
    }

    public void deepCompare(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        if (actualItem == null && expectedItem == null) {
            return;
        } else if (actualItem == null || expectedItem == null) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation()));
            return;
        } else if (actualItem.getClass() != expectedItem.getClass()) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation()));
            return;
        }
        final Class clazz = actualItem.getClass();

        switch (extractFieldType(clazz)) {
            case PRIMITIVE:
            case STRING:
            case ENUM:
            case OBJECT:
                if (!actualItem.equals(expectedItem)) {
                    assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation()));
                }
                return;
            case MAP:
                assertEqualityOfMapItems(actualItem, expectedItem, locationCreator);
                break;
            case COLLECTION:
                assertEqualityOfCollectionItems(actualItem, expectedItem, locationCreator);
                break;
            case ARRAY:
                assertEqualityOfArrayItems(actualItem, expectedItem, locationCreator);
                break;
            case DEEP_VERIFIABLE:
                compareFields(actualItem, expectedItem, locationCreator);
        }
    }

    private void compareFields(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        final Class clazz = actualItem.getClass();
        final Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!(isApiVerifiableForType(field) || isApiVerifiableOnClassLevel(clazz, field))) {
                log.debug("No verifying field:  " + variableInfo(clazz, field));
                continue;
            }
            field.setAccessible(true);
            Object actualObj = extractFieldValueFromObject(field, actualItem);
            Object expectedObj = extractFieldValueFromObject(field, expectedItem);
            deepCompare(actualObj, expectedObj, locationCreator.locationOfField(field));
        }
    }

    private void assertEqualityOfArrayItems(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        List<Object> actualArray = new ArrayList<>();
        List<Object> expectedArray = new ArrayList<>();
        int actualLength = Array.getLength(actualItem);
        for (int i = 0; i < actualLength; i++) {
            actualArray.add(Array.get(actualItem, i));
        }
        int expectedLength = Array.getLength(expectedItem);
        for (int i = 0; i < expectedLength; i++) {
            expectedArray.add(Array.get(expectedItem, i));
        }

        if (actualArray.size() != expectedArray.size()) {
            assertionCreator.fail(failMessageCreator(actualArray.size(), expectedArray.size(), locationCreator.getLocation(), actualItem.getClass()));
            return;
        }
        Iterator actIterator = actualArray.iterator();
        Iterator expIterator = expectedArray.iterator();
        int i = 0;
        while (actIterator.hasNext() && expIterator.hasNext()) {
            deepCompare(actIterator.next(), expIterator.next(), locationCreator.locationOnPosition(i++));
        }
    }

    private void assertEqualityOfMapItems(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        Map actualMap = (Map) actualItem;
        Map expectedMap = (Map) expectedItem;
        if (actualMap.size() != expectedMap.size()) {
            assertionCreator.fail(failMessageCreator(actualMap.size(), expectedMap.size(), locationCreator.getLocation(), actualItem.getClass()));
            return;
        }
        for (Map.Entry entry : ((Map<?, ?>) actualItem).entrySet()) {
            deepCompare(entry.getValue(), ((Map<?, ?>) expectedItem).get(entry.getKey()),
                    locationCreator.locationOnPosition(entry.getKey()));
        }
    }

    private void assertEqualityOfCollectionItems(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        Collection actualCollection = (Collection) actualItem;
        Collection expectedCollection = (Collection) expectedItem;
        if (actualCollection.size() != expectedCollection.size()) {
            assertionCreator.fail(failMessageCreator(actualCollection.size(), expectedCollection.size(),
                    locationCreator.getLocation(), actualItem.getClass()));
            return;
        }
        Iterator actIterator = actualCollection.iterator();
        Iterator expIterator = expectedCollection.iterator();
        if (actualItem instanceof Set) {
            boolean foundDiffs = false;
            while (actIterator.hasNext()) {
                Object actItem = actIterator.next();
                if (!expectedCollection.contains(actItem)) {
                    foundDiffs = true;
                    assertionCreator.fail(failMessageCreator(actItem, true, locationCreator.getLocation()));
                }
            }
            if (foundDiffs) {
                while (expIterator.hasNext()) {
                    Object expItem = expIterator.next();
                    if (!actualCollection.contains(expItem)) {
                        assertionCreator.fail(failMessageCreator(expItem, false, locationCreator.getLocation()));
                    }
                }
            }
        } else {
            int i = 0;
            while (actIterator.hasNext() && expIterator.hasNext()) {
                deepCompare(actIterator.next(), expIterator.next(), locationCreator.locationOnPosition(i++));
            }
        }
    }

    private Object extractFieldValueFromObject(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field " + field.toString());
        }
    }

}
