package com.testcraftsmanship.core.api;
import com.testcraftsmanship.core.annotations.Verifiable;
import com.testcraftsmanship.core.annotations.VerifiableExclude;
import com.testcraftsmanship.core.type.ReflectAssertionType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;


@Slf4j
public class DeepAssertions {
    private static final String PROJECT_PACKAGE = "com.testcraftsmanship";

    public boolean areEqualByAnnotatedFields(Object actualItem, Object expectedItem, ReflectAssertionType type) {
        try {
            assertEqualityOfAnnotatedFields(actualItem, expectedItem, type);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    public void assertEqualityOfAnnotatedFields(Object actualItem, Object expectedItem, ReflectAssertionType type) {
        final Class clazz = actualItem.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        SoftAssertions assertions = new SoftAssertions();

        for (Field field : fields) {
            if (!(isApiVerifiableForType(field, type) || isApiVerifiableOnClassLevel(clazz, field, type))) {
                log.debug("No verifying field:  " + variableInfo(clazz, field));
                continue;
            }
            field.setAccessible(true);
            Object actualObj = extractFieldValueFromObject(field, actualItem);
            Object expectedObj = extractFieldValueFromObject(field, expectedItem);
            if (actualObj == null && expectedObj == null) {
                continue;
            } else if (actualObj == null || expectedObj == null) {
                fail(variableInfo(clazz, field) + " has null in one of provided objects.");
            }
            if (actualObj.getClass().isEnum()) {
                assertions.assertThat(actualObj)
                        .isEqualTo(expectedObj);
            } else if (!isFromFigaroPackage(actualObj)) {
                assertions.assertThat(actualObj)
                        .withFailMessage(variableInfo(clazz, field)
                                + " has value " + actualObj + ", expect to have " + expectedObj)
                        .isEqualTo(expectedObj);
            } else if (isStringOrPrimitive(field)) {
                assertions.assertThat(actualObj)
                        .withFailMessage(variableInfo(clazz, field)
                                + " has value " + actualObj + ", expect to have " + expectedObj)
                        .isEqualTo(expectedObj);

            } else if (isCollection(field)) {
                assertEqualityOfCollectionItems(assertions, actualItem, expectedItem, type, field);
            } else {
                assertEqualityOfAnnotatedFields(actualObj, expectedObj, type);
            }
        }
        assertions.assertAll();
    }

    private void assertEqualityOfCollectionItems(SoftAssertions assertions, Object actualItem, Object expectedItem,
                                                 ReflectAssertionType type, Field collectionField) {
        final Class clazz = actualItem.getClass();
        final Collection actualCollection = (Collection) extractFieldValueFromObject(collectionField, actualItem);
        final Collection expectedCollection = (Collection) extractFieldValueFromObject(collectionField, expectedItem);
        if (actualCollection == null && expectedCollection == null) {
            return;
        }
        if (actualCollection == null) {
            assertions.fail(collectionInfo(clazz, collectionField) + " is null but should not be");
            return;
        } else if (expectedCollection == null) {
            assertions.fail(collectionInfo(clazz, collectionField) + " should be null but it is not");
            return;
        } else {
            if (actualCollection.size() != expectedCollection.size()) {
                assertions.fail(collectionInfo(clazz, collectionField) + " expect to have "
                        + expectedCollection.size() + " items but have "
                        + actualCollection.size() + " items");
                return;
            }
            Iterator actIterator = actualCollection.iterator();
            Iterator expIterator = expectedCollection.iterator();
            while (actIterator.hasNext() && expIterator.hasNext()) {
                assertEqualityOfAnnotatedFields(actIterator.next(), expIterator.next(), type);
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

    private boolean isFromFigaroPackage(Object actualObj) {
        return actualObj.getClass().getPackage().toString().contains(PROJECT_PACKAGE);
    }

    private boolean isStringOrPrimitive(Field field) {
        return field.getType().isPrimitive() || String.class.equals(field.getType());
    }

    private boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private String variableInfo(Class clazz, Field field) {
        return clazz.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName();
    }

    private String collectionInfo(Class clazz, Field field) {
        return clazz.getSimpleName() + ": Collection<" + clazz.getSimpleName() + "> " + field.getName();
    }

    private boolean isApiVerifiableForType(Field field, ReflectAssertionType type) {
        return field.isAnnotationPresent(Verifiable.class)
                && List.of(field.getAnnotation(Verifiable.class).type()).contains(type.getType());
    }

    private boolean isApiVerifiableOnClassLevel(Class clazz, Field field, ReflectAssertionType type) {
        boolean isFieldExcluded = field.isAnnotationPresent(VerifiableExclude.class)
                && List.of(field.getAnnotation(VerifiableExclude.class).type()).contains(type.getType());
        boolean isClassAnnotated = clazz.isAnnotationPresent(Verifiable.class)
                && List.of(((Verifiable) clazz.getAnnotation(Verifiable.class)).type()).
                contains(type.getType());
        return isClassAnnotated && !isFieldExcluded;
    }
}
