package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.assertions.AssertionCreator;
import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.text.CheckType;
import com.testcraftsmanship.deepassertions.core.text.LocationCreator;
import com.testcraftsmanship.deepassertions.core.text.UpdateInfo;
import lombok.AccessLevel;
import lombok.Getter;
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

import static com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor.extractFieldType;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.failMessageCreator;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.variableInfo;

@Slf4j
public abstract class DeepComparator {
    @Getter(value = AccessLevel.PROTECTED)
    private final Config config;
    @Getter(value = AccessLevel.PROTECTED)
    private final AssertionCreator assertionCreator;


    DeepComparator(Config config) {
        this.config = config;
        this.assertionCreator = new AssertionCreator();
    }

    void compare(Object actualItem, Object expectedItem, Class parentClass, LocationCreator locationCreator) {
        deepCompare(actualItem, expectedItem, parentClass, locationCreator);
        assertionCreator.performAssertions();
    }

    abstract boolean isDeepVerifiableField(Class parentClass, Field field);

    private void deepCompare(Object actualItem, Object expectedItem, Class parentClass, LocationCreator locationCreator) {
        Class currentClass = actualItem != null ? actualItem.getClass() : expectedItem.getClass();
        UpdateInfo updateInfo = new UpdateInfo(currentClass);
        deepCompare(actualItem, expectedItem, parentClass, locationCreator, updateInfo);
    }

    private void deepCompare(Object actualItem, Object expectedItem, Class parentClass,
                             LocationCreator locationCreator, UpdateInfo updateInfo) {
        if (actualItem == null && expectedItem == null) {
            return;
        } else if (actualItem == null || expectedItem == null) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation(), updateInfo));
            return;
        } else if (actualItem.getClass() != expectedItem.getClass()) {
            assertionCreator.fail(failMessageCreator(actualItem, expectedItem, locationCreator.getLocation(), updateInfo));
            return;
        }
        final Class fieldClass = actualItem.getClass();

        switch (extractFieldType(fieldClass)) {
            case PRIMITIVE:
            case STRING:
            case ENUM:
            case OBJECT:
                if (isProjectPackageClass(fieldClass)) {
                    compareFields(actualItem, expectedItem, fieldClass, locationCreator);
                } else {
                    log.debug("No deep verification of the field:  " + locationCreator.getLocation());
                    if (!actualItem.equals(expectedItem)) {
                        assertionCreator.fail(failMessageCreator(actualItem, expectedItem,
                                locationCreator.getLocation(), updateInfo));
                    }
                }
                return;
            case MAP:
                assertEqualityOfMapItems(actualItem, expectedItem, parentClass, locationCreator, updateInfo);
                break;
            case COLLECTION:
                assertEqualityOfCollectionItems(actualItem, expectedItem, parentClass, locationCreator, updateInfo);
                break;
            case ARRAY:
                assertEqualityOfArrayItems(actualItem, expectedItem, parentClass, locationCreator);
                break;
            default:
                log.warn("No deep verification of the field:  " + locationCreator.getLocation());
                if (!actualItem.equals(expectedItem)) {
                    assertionCreator.fail(failMessageCreator(actualItem, expectedItem,
                            locationCreator.getLocation(), updateInfo));
                }
        }
    }

    private void compareFields(Object actualItem, Object expectedItem, Class parentClass, LocationCreator locationCreator) {
        final List<Field> fields = extractFieldsFromClassAndSuperClass(parentClass, new ArrayList<>());

        for (Field field : fields) {
            field.setAccessible(true);
            Object actualObj = extractFieldValueFromObject(field, actualItem);
            Object expectedObj = extractFieldValueFromObject(field, expectedItem);
            //if should not be verified then continue;
            if (isDeepVerifiableField(parentClass, field)) {
                deepCompare(actualObj, expectedObj, parentClass, locationCreator.locationOfField(field));
            } else {
                log.debug("No deep verification of the field:  " + variableInfo(parentClass, field));
                if (!actualObj.equals(expectedObj)) {
                    UpdateInfo updateInfo = new UpdateInfo(actualObj.getClass());
                    assertionCreator.fail(failMessageCreator(actualObj, expectedObj,
                            locationCreator.locationOfField(field).getLocation(), updateInfo));
                }
            }

        }
    }

    private void assertEqualityOfArrayItems(Object actualItem, Object expectedItem,
                                            Class parentClass, LocationCreator locationCreator) {
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
                assertEqualityOfMapItems(actualMap, expectedMap, parentClass, locationCreator, updateInfo);
            }
        } else {
            for (int i = 0; i < actualLength; i++) {
                Object expectedElement = null;
                if (i < expectedLength) {
                    expectedElement = Array.get(expectedItem, i);
                }
                deepCompare(Array.get(actualItem, i), expectedElement, parentClass,
                        locationCreator.locationOnPosition(i), updateInfo);
            }
            for (int i = actualLength; i < expectedLength; i++) {
                deepCompare(null, Array.get(expectedItem, i), parentClass,
                        locationCreator.locationOnPosition(i), updateInfo);
            }
        }
    }

    private void assertEqualityOfCollectionItems(Object actualItem,
                                                 Object expectedItem,
                                                 Class parentClass,
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
                assertEqualityOfMapItems(actualMap, expectedMap, parentClass, locationCreator, updateInfo);
            }
        } else {
            Iterator actIterator = actualCollection.iterator();
            Iterator expIterator = expectedCollection.iterator();
            int i = 0;
            while (actIterator.hasNext() && expIterator.hasNext()) {
                deepCompare(actIterator.next(), expIterator.next(), parentClass, locationCreator.locationOnPosition(i++));
            }
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

    private void assertEqualityOfMapItems(Object actualItem, Object expectedItem, Class parentClass,
                                          LocationCreator locationCreator, UpdateInfo updateInfo) {
        Map actualMap = (Map) actualItem;
        Map expectedMap = (Map) expectedItem;
        if (actualMap.size() != expectedMap.size() && !updateInfo.getCheckType().equals(CheckType.COLLECTION_DUPLICATES)) {
            assertionCreator.fail(failMessageCreator(actualMap.size(), expectedMap.size(),
                    locationCreator.getLocation(), updateInfo));
        }
        for (Map.Entry entry : ((Map<?, ?>) actualItem).entrySet()) {
            updateInfo.setNumbersValidationKey(entry.getKey());
            deepCompare(entry.getValue(), ((Map<?, ?>) expectedItem).get(entry.getKey()), parentClass,
                    locationCreator.locationOnPosition(entry.getKey()), updateInfo);
        }
        Set<?> omittedKeys = new HashSet<>(expectedMap.keySet());
        omittedKeys.removeAll(actualMap.keySet());
        for (Object expectedKey : omittedKeys) {
            updateInfo.setNumbersValidationKey(expectedKey);
            deepCompare(((Map<?, ?>) actualItem).getOrDefault(expectedKey, null),
                    ((Map<?, ?>) expectedItem).get(expectedKey),
                    parentClass, locationCreator.locationOnPosition(expectedKey), updateInfo);
        }
    }

    private List<Field> extractFieldsFromClassAndSuperClass(Class parentClass, List<Field> fieldsList) {
        fieldsList.addAll(List.of(parentClass.getDeclaredFields()));
        Class superClass = parentClass.getSuperclass();
        if (superClass.equals(Object.class)) {
            return fieldsList;
        } else {
            return extractFieldsFromClassAndSuperClass(superClass, fieldsList);
        }
    }

    private Object extractFieldValueFromObject(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field " + field.toString());
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

    private boolean isProjectPackageClass(Class clazz) {
        return config.getDeepVerifiablePackages().stream().anyMatch(definedPackage -> clazz.getName().contains(definedPackage));
    }
}
