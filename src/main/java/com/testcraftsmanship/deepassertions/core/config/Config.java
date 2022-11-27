package com.testcraftsmanship.deepassertions.core.config;

import com.testcraftsmanship.deepassertions.core.api.items.DeepAssertType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Config {
    private static final String ITEMS_SEPARATOR = ",";
    private static final String PACKAGE_MATCHING_REGEXP = "^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_](\\s*"
            + ITEMS_SEPARATOR + "\\s*[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_])*$";
    private static final String ASSERT_TAGS_REGEXP = "^[a-zA-Z0-9_-]+(\\s*" + ITEMS_SEPARATOR + "\\s*[a-zA-Z0-9_-]+)*";
    private static final String ASSERT_TYPE_MATCHING_REGEXP = "^local$|^annotated$";
    public static final DeepAssertType DEFAULT_DEEP_ASSERT_TYPE = DeepAssertType.ANNOTATED;
    public static final String DEEP_VERIFIABLE_DEFAULT_PACKAGE = "com.testcraftsmanship";

    @Setter
    @Getter
    private boolean withAnyOrder = false;

    @Getter
    private List<String> deepVerifiablePackages = new ArrayList<>();

    @Getter
    private DeepAssertType deepAssertType;

    @Getter
    private boolean allFieldsIncludedByDefault;

    @Getter
    private List<String> assertIncludeTags = new ArrayList<>();


    public Config() {
        this.allFieldsIncludedByDefault = true;
        this.deepAssertType = extractDeepAssertType();
        this.deepVerifiablePackages.addAll(extractDeepAssertPackages());
        this.assertIncludeTags.addAll(extractAssertTags());
    }

    public void setDeepVerifiablePackages(String... deepVerifiablePackages) {
        this.deepAssertType = DeepAssertType.LOCAL;
        this.deepVerifiablePackages = List.of(deepVerifiablePackages);
    }

    public void excludeAllFieldsByDefault() {
        this.allFieldsIncludedByDefault = false;
    }

    public void setAssertIncludedTags(String... tags) {
        this.assertIncludeTags = List.of(tags);
    }

    private DeepAssertType extractDeepAssertType() {
        String defaultType = System.getProperty("DEEP_ASSERT_TYPE");
        if (defaultType != null && defaultType.toLowerCase().matches(ASSERT_TYPE_MATCHING_REGEXP)) {
            return DeepAssertType.parse(defaultType);
        } else {
            return DEFAULT_DEEP_ASSERT_TYPE;
        }
    }

    private List<String> extractDeepAssertPackages() {
        String defaultPackage = System.getProperty("DEEP_ASSERT_PACKAGES");
        if (defaultPackage != null && defaultPackage.matches(PACKAGE_MATCHING_REGEXP)) {
            return Arrays.stream(defaultPackage.split(ITEMS_SEPARATOR))
                    .map(String::trim).collect(Collectors.toList());
        } else {
            return List.of(DEEP_VERIFIABLE_DEFAULT_PACKAGE);
        }
    }

    private List<String> extractAssertTags() {
        String defaultPackage = System.getProperty("ASSERT_TAGS");
        if (defaultPackage != null && defaultPackage.matches(ASSERT_TAGS_REGEXP)) {
            return Arrays.stream(defaultPackage.split(ITEMS_SEPARATOR))
                    .map(String::trim).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
