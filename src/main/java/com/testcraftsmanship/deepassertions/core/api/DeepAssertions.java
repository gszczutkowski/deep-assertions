package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableForType;
import static com.testcraftsmanship.deepassertions.core.api.ObjectValidator.isApiVerifiableOnClassLevel;
import static com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor.extractFieldType;
import static com.testcraftsmanship.deepassertions.core.text.MessageCreator.variableInfo;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
public class DeepAssertions {
    private SoftAssertions assertions = new SoftAssertions();
    private Config config = new Config(DeepAssertType.LOCAL);
    private List<String> deepAssertTags = new ArrayList<>();

    public DeepAssertions() {
        deepAssertTags.add("item");
    }

    public void assertEquals(Object actualItem, Object expectedItem) {
        assertions = new SoftAssertions();
        String mainClassName = actualItem.getClass().getSimpleName();
        deepCompare(actualItem, expectedItem, mainClassName);
        assertions.assertAll();
    }

    public void deepCompare(Object actualItem, Object expectedItem, String currentItemLocation) {
        if (actualItem == null && expectedItem == null) {
            return;
        } else if (actualItem == null || expectedItem == null) {
            fail("Actual item of type " + currentItemLocation + " is " + actualItem + " but expected is " + expectedItem);
        } else if (actualItem.getClass() != expectedItem.getClass()) {
            fail("Actual item type is " + actualItem.getClass().getSimpleName()
                    + " but expected item type is " + expectedItem.getClass().getSimpleName());
        }
        final Class clazz = actualItem.getClass();

        switch (extractFieldType(clazz)) {
            case PRIMITIVE:
            case STRING:
            case ENUM:
            case OBJECT:
                assertions.assertThat(actualItem)
                        .withFailMessage(clazz.getSimpleName() + " " + currentItemLocation
                                + " has value " + actualItem + ", expect to have " + expectedItem)
                        .isEqualTo(expectedItem);
                return;
            case COLLECTION:
                assertEqualityOfCollectionItems(actualItem, expectedItem, currentItemLocation);
                break;
            case ARRAY:
                assertEqualityOfArrayItems(actualItem, expectedItem, currentItemLocation);
                break;
            case DEEP_VERIFIABLE:
                compareFields(actualItem, expectedItem, currentItemLocation);
        }
    }

    private void compareFields(Object actualItem, Object expectedItem, String currentItemLocation) {
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
            deepCompare(actualObj, expectedObj, currentItemLocation + "." + field.getName());
        }
    }

    private void assertEqualityOfArrayItems(Object actualItem, Object expectedItem, String currentItemLocation) {
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
            assertions.fail(currentItemLocation + " expect to have "
                    + expectedArray.size() + " items but have "
                    + actualArray.size() + " items");
            return;
        }
        Iterator actIterator = actualArray.iterator();
        Iterator expIterator = expectedArray.iterator();
        int i = 0;
        while (actIterator.hasNext() && expIterator.hasNext()) {
            deepCompare(actIterator.next(), expIterator.next(), currentItemLocation + "[" + i++ + "]");
        }
    }


    private void assertEqualityOfCollectionItems(Object actualItem, Object expectedItem, String currentItemLocation) {
        Collection actualCollection = (Collection) actualItem;
        Collection expectedCollection = (Collection) expectedItem;
        if (actualCollection.size() != expectedCollection.size()) {
            assertions.fail(currentItemLocation + " expect to have "
                    + expectedCollection.size() + " items but have "
                    + actualCollection.size() + " items");
            return;
        }
        Iterator actIterator = actualCollection.iterator();
        Iterator expIterator = expectedCollection.iterator();
        int i = 0; //How to proceed with Sets
        while (actIterator.hasNext() && expIterator.hasNext()) {
            deepCompare(actIterator.next(), expIterator.next(), currentItemLocation + "(" + i++ + ")");
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
