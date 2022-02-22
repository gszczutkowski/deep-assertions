package com.testcraftsmanship.deepassertions.core.api;

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
    boolean isDeepVerifiableField(Class parentClass, Field field) {
        return isDeepVerifiableClass(parentClass) && isDeepVerifiableField(field);
    }

    private boolean isDeepVerifiableClass(Class clazz) {
        return getConfig().getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> clazz.getPackage().toString().contains(packageName));
    }

    private boolean isDeepVerifiableField(Field field) {
        return getConfig().getDeepVerifiablePackages().stream()
                .anyMatch(packageName -> field.getName().contains(packageName))
                || field.getType().isArray()
                || Map.class.isAssignableFrom(field.getType())
                || Collection.class.isAssignableFrom(field.getType());
    }
}
