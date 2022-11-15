package com.testcraftsmanship.deepassertions.core.api.comparator;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.config.Config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DefinedDeepComparator extends DeepComparator {
    public DefinedDeepComparator(Config config) {
        super(config);
    }

    @Override
    public <T> boolean isDeepVerifiableField(Class<T> parentClass, Field field) {
        return isDeepVerifiableClass(parentClass) && !field.isAnnotationPresent(DeepVerifiableExclude.class)
                || isDeepVerifiableField(field);
    }

    private <T> boolean isDeepVerifiableClass(Class<T> clazz) {
        return verifiableWithDefinedAssertType(clazz);
    }

    private boolean isDeepVerifiableField(Field field) {
        return verifiableWithDefinedAssertType(field)
                || field.getType().isArray()
                || Map.class.isAssignableFrom(field.getType())
                || Collection.class.isAssignableFrom(field.getType());
    }

    private boolean verifiableWithDefinedAssertType(AnnotatedElement clazz) {
        if (clazz.isAnnotationPresent(DeepVerifiable.class) && getConfig().getDeepAssertTags() != null) {
            return Arrays
                    .stream(clazz.getAnnotation(DeepVerifiable.class).tags())
                    .anyMatch(tag -> getConfig().getDeepAssertTags().contains(tag));
        }
        return false;
    }
}