package com.testcraftsmanship.deepassertions.core.fields;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static com.testcraftsmanship.deepassertions.core.config.Config.PROJECT_PACKAGE;


public class FieldTypeExtractor {
    public static FieldType extractFieldType(Field field) {
        return extractFieldType(field.getType());
    }

    public static FieldType extractFieldType(Class clazz) {
        if (clazz.isPrimitive()) {
            return FieldType.PRIMITIVE;
        } else if (clazz.isArray()) {
            return FieldType.ARRAY;
        } else if (clazz.isEnum()) {
            return FieldType.ENUM;
        } else if (String.class.equals(clazz)) {
            return FieldType.STRING;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return FieldType.COLLECTION;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return FieldType.MAP;
        } else if (isDeepVerifiableObject(clazz)) {
            return FieldType.DEEP_VERIFIABLE;
        } else if (Object.class.isAssignableFrom(clazz)) {
            return FieldType.OBJECT;
        }
        throw new IllegalArgumentException("Field type not supported: " + clazz);
    }

    private static boolean isDeepVerifiableObject(Class clazz) {
        return clazz.getPackage().toString().contains(PROJECT_PACKAGE)
                && Object.class.isAssignableFrom(clazz);
    }
}
