package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.config.Config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public class AnnotatedDeepComparator extends DeepComparator {

    AnnotatedDeepComparator(Config config) {
        super(config);
    }

    @Override
    boolean isDeepVerifiableField(Class parentClass, Field field) {
        return isDeepVerifiable(parentClass, field);
    }

    @Override
    boolean isDeepVerifiableField(Class parentClass, Class fieldClass) {
        return isDeepVerifiable(parentClass, fieldClass);
    }

    private boolean isDeepVerifiable(AnnotatedElement parentClass, AnnotatedElement fieldClass) {
        return parentClass.isAnnotationPresent(DeepVerifiable.class) && !fieldClass.isAnnotationPresent(DeepVerifiableExclude.class)
                || fieldClass.isAnnotationPresent(DeepVerifiable.class);
    }
}
