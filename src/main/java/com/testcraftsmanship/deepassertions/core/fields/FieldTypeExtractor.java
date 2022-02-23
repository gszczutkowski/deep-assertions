package com.testcraftsmanship.deepassertions.core.fields;


import java.util.Collection;
import java.util.Map;


public final class FieldTypeExtractor {

    private FieldTypeExtractor() {
    }

    public static FieldType extractFieldType(Class fieldClazz) {
        if (fieldClazz.isPrimitive()) {
            return FieldType.PRIMITIVE;
        } else if (fieldClazz.isArray()) {
            return FieldType.ARRAY;
        } else if (fieldClazz.isEnum()) {
            return FieldType.ENUM;
        } else if (String.class.equals(fieldClazz)) {
            return FieldType.STRING;
        } else if (Collection.class.isAssignableFrom(fieldClazz)) {
            return FieldType.COLLECTION;
        } else if (Map.class.isAssignableFrom(fieldClazz)) {
            return FieldType.MAP;
        } else if (Object.class.isAssignableFrom(fieldClazz)) {
            return FieldType.OBJECT;
        }
        throw new IllegalArgumentException("Field type not supported: " + fieldClazz);
    }

}
