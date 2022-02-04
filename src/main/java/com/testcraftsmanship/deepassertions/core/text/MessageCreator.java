package com.testcraftsmanship.deepassertions.core.text;

import java.lang.reflect.Field;

public class MessageCreator {

    public static String variableInfo(Class clazz) {
        return clazz.getSimpleName();
    }

    public static String variableInfo(Class clazz, Field field) {
        return clazz.getSimpleName() + ": " + field.getType().getSimpleName() + " " + field.getName();
    }

    public static String collectionInfo(Class clazz, Field field) {
        return clazz.getSimpleName() + ": Collection<" + clazz.getSimpleName() + "> " + field.getName();
    }
}
