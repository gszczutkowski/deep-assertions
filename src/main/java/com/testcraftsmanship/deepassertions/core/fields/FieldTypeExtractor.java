package com.testcraftsmanship.deepassertions.core.fields;


import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertType;
import com.testcraftsmanship.deepassertions.core.config.Config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
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

    public boolean isDeepVerifiableClass(Class clazz) {
        return verifiableWithLocalAssertType(clazz)
                || verifiableWithDefinedAssertType(clazz)
                || verifiableWithAnnotatedAssertType(clazz);
    }


    public boolean isDeepVerifiableField(Field field) {
        return verifiableFieldWithLocalAssertType(field)
                || verifiableWithDefinedAssertType(field)
                || verifiableWithAnnotatedAssertType(field)
                || field.getType().isArray()
                || Map.class.isAssignableFrom(field.getType())
                || Collection.class.isAssignableFrom(field.getType());
    }

    private boolean verifiableFieldWithLocalAssertType(Field field) {
        return config.getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> field.getName().contains(packageName))
                && DeepAssertType.LOCAL.equals(config.getDeepAssertType());
    }

    private boolean verifiableWithLocalAssertType(Class clazz) {
        return config.getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> clazz.getPackage().toString().contains(packageName))
                && DeepAssertType.LOCAL.equals(config.getDeepAssertType());
    }

    private boolean verifiableWithDefinedAssertType(AnnotatedElement clazz) {
        if (clazz.isAnnotationPresent(DeepVerifiable.class) && config.getDeepAssertTags() != null) {
            boolean isAnnotatedWithRequiredTag = Arrays
                    .stream(((DeepVerifiable) clazz.getAnnotation(DeepVerifiable.class)).tags())
                    .anyMatch(tag -> config.getDeepAssertTags().contains(tag));
            boolean isDefinedAssertionType = DeepAssertType.DEFINED.equals(config.getDeepAssertType());
            return isAnnotatedWithRequiredTag && isDefinedAssertionType;
        }
        return false;
    }

    private boolean verifiableWithAnnotatedAssertType(AnnotatedElement clazz) {
        return clazz.isAnnotationPresent(DeepVerifiable.class)
                && DeepAssertType.ANNOTATED.equals(config.getDeepAssertType());
    }
}
