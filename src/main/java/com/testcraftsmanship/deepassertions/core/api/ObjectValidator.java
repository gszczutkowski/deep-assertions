package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.annotations.VerifiableExclude;

import java.lang.reflect.Field;

public final class ObjectValidator {

    private ObjectValidator() {
    }

    public static boolean isApiVerifiableForType(Field field/*, ReflectAssertionType type*/) {
        return field.isAnnotationPresent(Verifiable.class);
        //&& List.of(field.getAnnotation(Verifiable.class).type()).contains(type.getType());
    }

    public static boolean isApiVerifiableOnClassLevel(Class clazz, Field field/*, ReflectAssertionType type*/) {
        boolean isFieldExcluded = field.isAnnotationPresent(VerifiableExclude.class);
        //&& List.of(field.getAnnotation(VerifiableExclude.class).type()).contains(type.getType());
        boolean isClassAnnotated = clazz.isAnnotationPresent(Verifiable.class);
        //&& List.of(((Verifiable) clazz.getAnnotation(Verifiable.class)).type()).
        //contains(type.getType());
        return isClassAnnotated && !isFieldExcluded;
    }

    /*    public static boolean shouldUseDeepAssert(Class clazz, DeepAssertType deepAssertType) {
        return (deepAssertType.equals(DeepAssertType.LOCAL)
                && (isCollectionWithObjectsFromLocalPackage(clazz) || isObjectFromLocalPackage(clazz)))
                || (deepAssertType.equals(DeepAssertType.DEFINED)
                && (isCollectionWithObjectsFromDefinedPackage(clazz) || isObjectFromDefinedPackage(clazz)));
    }*/

    /*    private static boolean isObjectFromDefinedPackage(Class clazz) {
        return clazz.getPackage() != null
                && DEFINED_PACKAGES.stream().anyMatch(defined -> defined.startsWith(clazz.getPackage().getName()));
    }

    private static boolean isCollectionWithObjectsFromDefinedPackage(Class clazz) {
        return clazz.getComponentType() != null
                &&  DEFINED_PACKAGES.stream().anyMatch(defined -> defined.startsWith(clazz.getComponentType().getName()));
    }

    private static boolean isObjectFromLocalPackage(Class clazz) {
        return clazz.getPackage() != null && clazz.getPackage().getName().startsWith(PROJECT_PACKAGE);
    }

    private static boolean isCollectionWithObjectsFromLocalPackage(Class clazz) {
        return clazz.getComponentType() != null && clazz.getComponentType().getName().startsWith(PROJECT_PACKAGE);
    }*/
}
