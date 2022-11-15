package com.testcraftsmanship.deepassertions.core.api.comparator;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.config.Config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public class AnnotatedDeepComparator extends DeepComparator {

    public AnnotatedDeepComparator(Config config) {
        super(config);
    }

    @Override
    public <T> boolean isDeepVerifiableField(Class<T> parentClass, Field field) {
        return isDeepVerifiable(parentClass, field);
    }

    private boolean isDeepVerifiable(AnnotatedElement parentClass, Field field) {
        return parentClass.isAnnotationPresent(DeepVerifiable.class)
                && !field.isAnnotationPresent(DeepVerifiableExclude.class)
                || field.isAnnotationPresent(DeepVerifiable.class);
    }
}
