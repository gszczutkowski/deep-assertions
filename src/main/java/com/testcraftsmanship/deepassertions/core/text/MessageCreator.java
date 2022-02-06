package com.testcraftsmanship.deepassertions.core.text;

import java.lang.reflect.Field;

import static com.testcraftsmanship.deepassertions.core.text.LocationCreator.classNameExtractor;

public final class MessageCreator {
    private MessageCreator() {
    }

    public static String failMessageCreator(Object actual, Object expected, String depth) {
        if (actual == null && expected == null) {
            throw new IllegalArgumentException("Failure message can not be generated as both objects are null");
        } else if (actual == null || expected == null) {
            String className = actual == null ? expected.getClass().getSimpleName() : actual.getClass().getSimpleName();
            return String.format("%s<%s> - actual object is %s but expected is %s", depth, className, actual, expected);

        } else if (actual.getClass() != expected.getClass()) {
            return String.format("%s - actual object type is %s but expected object type is %s",
                    depth, actual.getClass().getSimpleName(), expected.getClass().getSimpleName());
        } else {
            return String.format("%s<%s> - actual object has value %s, expect to have %s",
                    depth, actual.getClass().getSimpleName(), actual, expected);
        }
    }

    public static String failMessageCreator(Object object, boolean isObjectActual, String depth) {
        if (object == null) {
            throw new IllegalArgumentException("Failure message can not be generated as passed object is null");
        } else {
            String className = object.getClass().getSimpleName();
            if (isObjectActual) {
                return String.format("%s<%s> - actual set has value {%s}, expected set don't have it", depth, className, object);
            } else {
                return String.format("%s<%s> - expected set has value {%s}, actual set don't have it", depth, className, object);
            }
        }
    }

    public static String failMessageCreator(int actualSize, int expectedSize, String depth, Class clazz) {
        String className = classNameExtractor(clazz);
        return String.format("%s<%s> - actual object has size %d but expected to have size %s",
                depth, className, actualSize, expectedSize);
    }

    public static String variableInfo(Class clazz) {
        return clazz.getSimpleName();
    }

    public static String variableInfo(Class clazz, Field field) {
        return clazz.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName();
    }
}
