package com.testcraftsmanship.deepassertions.core.text;

import com.testcraftsmanship.deepassertions.core.fields.FieldType;

import java.lang.reflect.Field;

import static com.testcraftsmanship.deepassertions.core.fields.FieldType.COLLECTION;
import static com.testcraftsmanship.deepassertions.core.fields.FieldType.MAP;
import static com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor.extractFieldType;

public class LocationCreator {
    private String location;

    public LocationCreator(Class rootClass) {
        this.location = createLocation(rootClass);
    }

    private LocationCreator(String fullPath) {
        this.location = fullPath;
    }

    public static String classNameExtractor(Class clazz) {
        String className = clazz.getSimpleName();
        if (clazz.getName().contains("java.util.ImmutableCollection")) {
            className = "Immutable" + className.replaceAll("([0-9]+|N)$", "");
        }
        return className;
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

    public String getLocation() {
        return location;
    }

    private String createLocation(Class clazz) {
        String className = classNameExtractor(clazz);
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
