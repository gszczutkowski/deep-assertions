package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.assertions.AssertionCreator;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor;
import com.testcraftsmanship.deepassertions.core.text.CheckType;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import com.testcraftsmanship.deepassertions.core.text.UpdateInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;

import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.failMessageCreator;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.variableInfo;

@Slf4j
public class DeepComparator {
    private final Config config;
    private final FieldTypeExtractor fieldTypeExtractor;
    private AssertionCreator assertionCreator;
    private List<String> deepAssertTags;


    DeepComparator(Config config) {
        this.config = config;
        this.fieldTypeExtractor = new FieldTypeExtractor(config);
        this.assertionCreator = new AssertionCreator();
        this.deepAssertTags = new ArrayList<>();
    }

    void compare(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        deepCompare(actualItem, expectedItem, locationCreator);
        assertionCreator.performAssertions();
    }

    private void deepCompare(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        UpdateInfo updateInfo = new UpdateInfo(actualItem.getClass());
        deepCompare(actualItem, expectedItem, locationCreator, updateInfo);
    }

    private void deepCompare(Object actualItem, Object expectedItem, LocationCreator locationCreator, UpdateInfo updateInfo) {
        if (actualItem == null && expectedItem == null) {
            return;
        } else if (actualItem == null || expectedItem == null) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation(), updateInfo));
            return;
        } else if (actualItem.getClass() != expectedItem.getClass()) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation(), updateInfo));
            return;
        }
        final Class clazz = actualItem.getClass();

        switch (fieldTypeExtractor.extractFieldType(clazz)) {
            case PRIMITIVE:
            case STRING:
            case ENUM:
            case OBJECT:
                if (!actualItem.equals(expectedItem)) {
                    assertionCreator.fail(failMessageCreator(actualItem, expectedItem,
                            locationCreator.getLocation(), updateInfo));
                }
                return;
            case MAP:
                assertEqualityOfMapItems(actualItem, expectedItem, locationCreator, updateInfo);
                break;
            case COLLECTION:
                assertEqualityOfCollectionItems(actualItem, expectedItem, locationCreator, updateInfo);
                break;
            case ARRAY:
                assertEqualityOfArrayItems(actualItem, expectedItem, locationCreator);
                break;
            case DEEP_VERIFIABLE:
                compareFields(actualItem, expectedItem, locationCreator);
                break;
            default:
                throw new IllegalStateException("Field type not supported");
        }
    }

    private void compareFields(Object actualItem, Object expectedItem, LocationCreator locationCreator) {
        final Class clazz = actualItem.getClass();
        final Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!(ObjectValidator.isApiVerifiableForType(field)
                    || ObjectValidator.isApiVerifiableOnClassLevel(clazz, field))) {
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
        int actualLength = Array.getLength(actualItem);
        int expectedLength = Array.getLength(expectedItem);
        UpdateInfo updateInfo = new UpdateInfo(actualItem.getClass());
        if (actualLength != expectedLength) {
            assertionCreator.fail(failMessageCreator(actualLength, expectedLength,
                    locationCreator.getLocation(), updateInfo));
        }
        if (config.isWithAnyOrder()) {
            Map<Object, Long> actualMap = new HashMap();
            Map<Object, Long> expectedMap = new HashMap();
            for (int i = 0; i < actualLength; i++) {
                Object actualElement = Array.get(actualItem, i);
                if (actualMap.keySet().contains(actualElement)) {
                    actualMap.put(actualElement, actualMap.get(actualElement) + 1);
                } else {
                    actualMap.put(actualElement, 1L);
                }
            }
            for (int i = 0; i < expectedLength; i++) {
                Object expectedElement = Array.get(expectedItem, i);
                if (expectedMap.keySet().contains(expectedElement)) {
                    expectedMap.put(expectedElement, expectedMap.get(expectedElement) + 1);
                } else {
                    expectedMap.put(expectedElement, 1L);
                }
            }
            if (actualMap.size() == actualLength && expectedMap.size() == expectedLength) {
                assertEqualityOfSets(actualMap.keySet(), expectedMap.keySet(), locationCreator);
            } else {
                updateInfo.setCollectionDuplicatesIfNotSet(actualItem.getClass(), extractItemClass(actualMap.keySet()));
                assertEqualityOfMapItems(actualMap, expectedMap, locationCreator, updateInfo);
            }
        } else {
            for (int i = 0; i < actualLength; i++) {
                Object expectedElement = null;
                if (i < expectedLength) {
                    expectedElement = Array.get(expectedItem, i);
                }
                deepCompare(Array.get(actualItem, i), expectedElement, locationCreator.locationOnPosition(i), updateInfo);
            }
            for (int i = actualLength; i < expectedLength; i++) {
                deepCompare(null, Array.get(expectedItem, i), locationCreator.locationOnPosition(i), updateInfo);
            }
        }
    }

    private void assertEqualityOfCollectionItems(Object actualItem,
                                                 Object expectedItem,
                                                 LocationCreator locationCreator,
                                                 UpdateInfo updateInfo) {
        Collection actualCollection = (Collection) actualItem;
        Collection expectedCollection = (Collection) expectedItem;
        if (actualCollection.size() != expectedCollection.size()) {
            assertionCreator.fail(failMessageCreator(actualCollection.size(), expectedCollection.size(),
                    locationCreator.getLocation(), updateInfo));
        }
        if (actualItem instanceof Set) {
            assertEqualityOfSets(actualCollection, expectedCollection, locationCreator);
        } else if (config.isWithAnyOrder()) {
            Set actualSet = new HashSet(actualCollection);
            Set expectedSet = new HashSet(expectedCollection);
            if (actualSet.size() == actualCollection.size() && expectedSet.size() == expectedCollection.size()) {
                assertEqualityOfSets(actualSet, expectedSet, locationCreator);
            } else {
                Map<Object, Long> actualMap = new HashMap();
                for (Object item : actualSet) {
                    actualMap.put(item, actualCollection.stream().filter(el -> el.equals(item)).count());
                }
                Map<Object, Long> expectedMap = new HashMap();
                for (Object item : expectedSet) {
                    expectedMap.put(item, expectedCollection.stream().filter(el -> el.equals(item)).count());
                }
                updateInfo.setCollectionDuplicatesIfNotSet(actualItem.getClass(), extractItemClass(actualSet));
                assertEqualityOfMapItems(actualMap, expectedMap, locationCreator, updateInfo);
            }
        } else {
            Iterator actIterator = actualCollection.iterator();
            Iterator expIterator = expectedCollection.iterator();
            int i = 0;
            while (actIterator.hasNext() && expIterator.hasNext()) {
                deepCompare(actIterator.next(), expIterator.next(), locationCreator.locationOnPosition(i++));
            }
        }
    }

    private Class extractItemClass(Object o) {
        if (o.getClass().isArray() && Array.getLength(o) > 0) {
            return Array.get(o, 0).getClass();
        } else if (o instanceof Set && !((Set<?>) o).isEmpty()) {
            return ((Set<?>) o).iterator().next().getClass();
        } else {
            log.warn("Unable to extract element type from Set");
            return Object.class;
        }
    }

    private void assertEqualityOfSets(Collection actualCollection, Collection expectedCollection,
                                      LocationCreator locationCreator) {
        Iterator actIterator = actualCollection.iterator();
        boolean foundDiffs = false;
        while (actIterator.hasNext()) {
            Object actItem = actIterator.next();
            if (!expectedCollection.contains(actItem)) {
                foundDiffs = true;
                assertionCreator.fail(failMessageCreator(actItem, true, locationCreator.getLocation()));
            }
        }
        if (foundDiffs) {
            Iterator expIterator = expectedCollection.iterator();
            while (expIterator.hasNext()) {
                Object expItem = expIterator.next();
                if (!actualCollection.contains(expItem)) {
                    assertionCreator.fail(failMessageCreator(expItem, false, locationCreator.getLocation()));
                }
            }
        }
    }

    private void assertEqualityOfMapItems(Object actualItem, Object expectedItem,
                                          LocationCreator locationCreator, UpdateInfo updateInfo) {
        Map actualMap = (Map) actualItem;
        Map expectedMap = (Map) expectedItem;
        if (actualMap.size() != expectedMap.size() && !updateInfo.getCheckType().equals(CheckType.COLLECTION_DUPLICATES)) {
            assertionCreator.fail(failMessageCreator(actualMap.size(), expectedMap.size(),
                    locationCreator.getLocation(), updateInfo));
        }
        for (Map.Entry entry : ((Map<?, ?>) actualItem).entrySet()) {
            updateInfo.setNumbersValidationKey(entry.getKey());
            deepCompare(entry.getValue(), ((Map<?, ?>) expectedItem).get(entry.getKey()),
                    locationCreator.locationOnPosition(entry.getKey()), updateInfo);
        }
        Set<?> omittedKeys = new HashSet<>(expectedMap.keySet());
        omittedKeys.removeAll(actualMap.keySet());
        for (Object expectedKey : omittedKeys) {
            updateInfo.setNumbersValidationKey(expectedKey);
            deepCompare(((Map<?, ?>) actualItem).get(expectedKey), ((Map<?, ?>) expectedItem).get(expectedKey),
                    locationCreator.locationOnPosition(expectedKey), updateInfo);
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
