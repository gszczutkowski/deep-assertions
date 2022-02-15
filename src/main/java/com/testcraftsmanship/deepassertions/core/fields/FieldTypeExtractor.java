package com.testcraftsmanship.deepassertions.core.fields;


import com.testcraftsmanship.deepassertions.core.api.DeepAssertType;
import com.testcraftsmanship.deepassertions.core.config.Config;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;


public class FieldTypeExtractor {

    private final Config config;

    public FieldTypeExtractor(Config config) {
        this.config = config;
    }

    public FieldType extractFieldType(Field field) {
        return extractFieldType(field.getType());
    }

    public FieldType extractFieldType(Class clazz) {
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

    public boolean isDeepVerifiableObject(Class clazz) {
        return config.getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> clazz.getPackage().toString().contains(packageName))
                && DeepAssertType.LOCAL.equals(config.getDeepAssertType());
    }
}
