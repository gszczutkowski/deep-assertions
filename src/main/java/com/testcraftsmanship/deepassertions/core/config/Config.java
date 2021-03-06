package com.testcraftsmanship.deepassertions.core.config;

import com.testcraftsmanship.deepassertions.core.api.DeepAssertType;
import com.testcraftsmanship.deepassertions.core.api.ValidationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final DeepAssertType DEFAULT_DEEP_ASSERT_TYPE = DeepAssertType.ANNOTATED;
    public static final ValidationType DEFAULT_VALIDATION_TYPE = ValidationType.ALL_BY_DEFAULT;
    public static final String DEEP_VERIFIABLE_DEFAULT_PACKAGE = "com.testcraftsmanship";

    @Setter
    @Getter
    private boolean withAnyOrder = false;

    @Getter
    private List<String> deepAssertTags = new ArrayList<>();

    @Getter
    private List<String> deepVerifiablePackages = new ArrayList<>();

    @Getter
    private DeepAssertType deepAssertType;

    @Getter
    private ValidationType validationType;

    public Config() {
        this.deepAssertType = DEFAULT_DEEP_ASSERT_TYPE;
        this.validationType = DEFAULT_VALIDATION_TYPE;
        deepVerifiablePackages.add(DEEP_VERIFIABLE_DEFAULT_PACKAGE);
    }

    public void setDeepAssertTags(String... deepAssertTags) {
        this.deepAssertType = DeepAssertType.DEFINED;
        this.deepAssertTags = List.of(deepAssertTags);
    }

    public void setDeepVerifiablePackages(String... deepVerifiablePackages) {
        this.deepAssertType = DeepAssertType.LOCAL;
        this.deepVerifiablePackages = List.of(deepVerifiablePackages);
    }
}
