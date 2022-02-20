package com.testcraftsmanship.deepassertions.core.fields;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.config.Config;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class FieldsTypeExtractorTest {

    @DeepVerifiable
    class ClassA {
        private String fieldA;
    }

    class ClassB {
        @DeepVerifiable
        private String fieldA;
        private String fieldB;
    }

    class ClassC {
        private String fieldA;
        private ClassD classDField;
    }

    class ClassD {
    }

    @Test
    public void localTypeShouldWorkForDefinedPackagesOnly() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);

        Class externalPackageField = new ClassC().getClass().getDeclaredField("fieldA").getType();
        boolean isExternalDeepVerifiable = fieldTypeExtractor.isDeepVerifiableClass(externalPackageField);
        org.assertj.core.api.Assertions.assertThat(isExternalDeepVerifiable).isFalse();
    }

    @Test
    public void localTypeShouldNotWorkForUndefinedPackages() throws NoSuchFieldException {
        Config config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);

        Class localPackageField = new ClassC().getClass().getDeclaredField("classDField").getType();
        boolean isLocalDeepVerifiable = fieldTypeExtractor.isDeepVerifiableClass(localPackageField);
        org.assertj.core.api.Assertions.assertThat(isLocalDeepVerifiable).isTrue();
    }

    @Test
    public void byDefaultAnnotationTypeShouldBeSet() {
        Config config = new Config();
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);
        org.assertj.core.api.Assertions.assertThat(fieldTypeExtractor.isDeepVerifiableClass(ClassA.class)).isTrue();
    }

    @Test
    public void fieldInAnnotatedClassShouldBeVerifiable() {
        Config config = new Config();
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);

        boolean isDeepVerifiableClass = fieldTypeExtractor.isDeepVerifiableClass(ClassA.class);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiableClass).isTrue();
    }

    @Test
    public void byDefaultAnnotationTypeShouldBeSetNegative() {
        Config config = new Config();
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);
        org.assertj.core.api.Assertions.assertThat(fieldTypeExtractor.isDeepVerifiableClass(ClassB.class)).isFalse();
    }

    @Test
    public void fieldNotAnnotatedOnClassShouldNotBeVerifiable() throws NoSuchFieldException {
        Config config = new Config();
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);

        Class annotatedOnClassLevel = new ClassB().getClass().getDeclaredField("fieldB").getType();
        boolean isDeepVerifiable = fieldTypeExtractor.isDeepVerifiableClass(annotatedOnClassLevel);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiable).isFalse();
    }

    @Test
    public void annotatedFieldNotOnClassShouldNotBeVerifiable() throws NoSuchFieldException {
        Config config = new Config();
        FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor(config);

        Field annotatedField = new ClassB().getClass().getDeclaredField("fieldA");
        boolean isDeepVerifiable = fieldTypeExtractor.isDeepVerifiableField(annotatedField);
        org.assertj.core.api.Assertions.assertThat(isDeepVerifiable).isTrue();
    }
}
