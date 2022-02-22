package com.testcraftsmanship.deepassertions.core.fields;


import com.testcraftsmanship.deepassertions.core.config.Config;

import java.util.Collection;
import java.util.Map;


public class FieldTypeExtractor {

    private final Config config;

    public FieldTypeExtractor(Config config) {
        this.config = config;
    }

    public FieldType extractFieldType(Class fieldClazz) {
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
