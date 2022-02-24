package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiableExclude;
import com.testcraftsmanship.deepassertions.core.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class LocalDeepComparator extends DeepComparator {

    LocalDeepComparator(Config config) {
        super(config);
    }

    @Override
    <T> boolean isDeepVerifiableField(Class<T> parentClass, Field field) {
        return isDeepVerifiableClass(parentClass) && isDeepVerifiableField(field)
                && !field.isAnnotationPresent(DeepVerifiableExclude.class);
    }

    private <T> boolean isDeepVerifiableClass(Class<T> clazz) {
        return getConfig().getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> clazz.getPackage().toString().contains(packageName));
    }

    private boolean isDeepVerifiableField(Field field) {
        return getConfig().getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> field.getType().getName().contains(packageName))
                || field.getType().isArray()
                || Map.class.isAssignableFrom(field.getType())
                || Collection.class.isAssignableFrom(field.getType());
    }
}
