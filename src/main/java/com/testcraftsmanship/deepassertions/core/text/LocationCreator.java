package com.testcraftsmanship.deepassertions.core.text;

import com.testcraftsmanship.deepassertions.core.config.Config;
import com.testcraftsmanship.deepassertions.core.fields.FieldType;
import com.testcraftsmanship.deepassertions.core.fields.FieldTypeExtractor;
import lombok.Getter;

import java.lang.reflect.Field;

import static com.testcraftsmanship.deepassertions.core.fields.FieldType.COLLECTION;
import static com.testcraftsmanship.deepassertions.core.fields.FieldType.MAP;

@Getter
public class LocationCreator {
    private final FieldTypeExtractor fieldTypeExtractor;
    private String location;
    private int level = 0;

    public LocationCreator(Config config, Class rootClass) {
        this.fieldTypeExtractor = new FieldTypeExtractor(config);
        this.location = createLocation(rootClass);
        level++; //this may work wrong for fields
    }

    private LocationCreator(FieldTypeExtractor fieldTypeExtractor, String fullPath) {
        this.fieldTypeExtractor = fieldTypeExtractor;
        this.location = fullPath;
        level++;
    }

    public static String classNameExtractor(Class clazz) {
        String className = clazz.getSimpleName();
        if (clazz.getName().contains("java.util.ImmutableCollection")) {
            className = "Immutable" + className.replaceAll("([0-9]+|N)$", "");
        }
        return className;
    }

    public LocationCreator locationOfField(Field field) {
        level++;
        return new LocationCreator(this.fieldTypeExtractor, location + "." + createFieldLocation(field));
    }

    public LocationCreator locationOnPosition(Object position) {
        if (location.endsWith("()")) {
            return new LocationCreator(this.fieldTypeExtractor, location.replace("()", "(" + position + ")"));
        } else if (location.endsWith("[]")) {
            return new LocationCreator(this.fieldTypeExtractor, location.replace("[]", "[" + position + "]"));
        } else {
            throw new IllegalStateException("Unable to set position for current object: " + location);
        }
    }

    private String createLocation(Class clazz) {
        String className = classNameExtractor(clazz);
        FieldType type = fieldTypeExtractor.extractFieldType(clazz);
        if (type.equals(MAP) || type.equals(COLLECTION)) {
            return className + "()";
        }
        return className;
    }

    private String createFieldLocation(Field field) {
        switch (fieldTypeExtractor.extractFieldType(field.getType())) {
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
