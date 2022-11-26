package com.testcraftsmanship.deepassertions.core.text;

import com.testcraftsmanship.deepassertions.core.fields.FieldType;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.testcraftsmanship.deepassertions.core.fields.FieldType.COLLECTION;
import static com.testcraftsmanship.deepassertions.core.fields.FieldType.MAP;
import static com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor.extractFieldType;


public class LocationCreator {
    @Getter
    private String location;

    public LocationCreator(Class rootClass) {
        this.location = createLocation(rootClass);
    }

    private LocationCreator(String fullPath) {
        this.location = fullPath;
    }

    public static String normalizeClassName(Class clazz) {
        String className = clazz.getSimpleName();
        if (clazz.getName().contains("java.util.ImmutableCollection")) {
            className = "Immutable" + className.replaceAll("([0-9]+|N)$", "");
        }
        return className;
    }

    public static String extractItemClassName(Object o) {
        if (o instanceof Collection && !((Collection<?>) o).isEmpty()) {
            String itemClassName = ((Collection<?>) o).iterator().next().getClass().getSimpleName();
            return String.format("%s<%s>", normalizeClassName(o.getClass()), itemClassName);
        } else if (o instanceof Collection) {
            return normalizeClassName(o.getClass());
        } else {
            return o.getClass().getSimpleName();
        }
    }

    public LocationCreator locationOfField(Field field) {
        return new LocationCreator(location + "." + createFieldLocation(field));
    }

    public LocationCreator locationOnPosition(Object position) {
        if (location.endsWith("()")) {
            return new LocationCreator(location.replace("()", "(" + position + ")"));
        } else if (location.endsWith("[]")) {
            return new LocationCreator(location.replace("[]", "[" + position + "]"));
        } else {
            throw new IllegalStateException("Unable to set position for current object: " + location);
        }
    }

    private String createLocation(Class clazz) {
        String className = normalizeClassName(clazz);
        FieldType type = extractFieldType(clazz);
        if (type.equals(MAP) || type.equals(COLLECTION)) {
            return className + "()";
        }
        return className;
    }

    private String createFieldLocation(Field field) {
        switch (extractFieldType(field.getType())) {
            case MAP:
            case COLLECTION:
                return field.getName() + "()";
            case ARRAY:
                return field.getName() + "[]";
            default:
                return field.getName();
        }
    }
}
