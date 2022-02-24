package com.testcraftsmanship.deepassertions.core.text;

import com.testcraftsmanship.deepassertions.core.config.Messages;

import java.lang.reflect.Field;

import static com.testcraftsmanship.deepassertions.core.text.LocationCreator.classNameExtractor;

public final class MessageCreator {
    private MessageCreator() {
    }

    public static String failMessageCreator(Object actual, Object expected, String depth, ActualObjectState actualObjectState) {
        if (actualObjectState.getCheckType().equals(CheckType.COLLECTION_DUPLICATES)) {
            expected = expected == null ? 0 : expected;
            String elementType = extractCollectionElementType(actualObjectState);
            return String.format(Messages.DIFFERENT_NUMBER_WITH_VALUE,
                    depth, elementType, actualNumberToMessage(actual), actualObjectState.getNumbersValidationKey(), expected);
        }
        if (actual == null && expected == null) {
            throw new IllegalArgumentException("Failure message can not be generated as both objects are null");
        } else if (actual == null || expected == null) {
            String className = actual == null ? expected.getClass().getSimpleName() : actual.getClass().getSimpleName();
            return String.format(Messages.DIFFERENT_VALUES, depth, className, actual, expected);
        } else if (actual.getClass() != expected.getClass()) {
            return String.format(Messages.DIFFERENT_TYPES,
                    depth, actual.getClass().getSimpleName(), expected.getClass().getSimpleName());
        } else {
            return String.format(Messages.DIFFERENT_VALUES,
                    depth, actual.getClass().getSimpleName(), actual, expected);
        }
    }

    public static String failMessageCreator(Object object, boolean isObjectActual, String depth) {
        if (object == null) {
            throw new IllegalArgumentException("Failure message can not be generated as passed object is null");
        }
        String className = object.getClass().getSimpleName();
        if (isObjectActual) {
            return String.format(Messages.DIFFERENCE_WITH_NO_EXPECTED_VALUE, depth, className, object);
        } else {
            return String.format(Messages.DIFFERENCE_WITH_NO_ACTUAL_VALUE, depth, className, object);
        }
    }

    public static String failMessageCreator(int actualSize, int expectedSize, String depth, ActualObjectState actualObjectState) {
        return String.format(Messages.DIFFERENT_COLLECTIONS_SIZES,
                depth, classNameExtractor(actualObjectState.getRealClass()), actualNumberToMessage(actualSize), expectedSize);
    }

    public static String variableInfo(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static String variableInfo(Class<?> clazz, Field field) {
        return clazz.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName();
    }

    private static String extractCollectionElementType(ActualObjectState actualObjectState) {
        String elementType = "";
        if (!actualObjectState.getRealClass().isArray()) {
            elementType = "<" + actualObjectState.getItemClass().getSimpleName() + ">";
        }
        return elementType;
    }

    private static String actualNumberToMessage(Object actual) {
        if (actual == null) {
            return "are no items";
        } else if (actual instanceof Number && Long.parseLong(actual.toString()) == 1) {
            return "is 1 item";
        } else if (actual instanceof Number) {
            return String.format("are %s items", actual);
        }
        throw new IllegalArgumentException("Object should extends from Number");
    }
}
