package com.testcraftsmanship.deepassertions.core.config;

import com.testcraftsmanship.deepassertions.core.api.DeepAssertType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final String DEFAULT_PACKAGE = "com.testcraftsmanship";

    @Setter
    @Getter
    private boolean withAnyOrder = false;

    @Getter
    private List<String> deepAssertTags = new ArrayList<>();

    @Getter
    private List<String> deepVerifiablePackages = List.of(DEFAULT_PACKAGE);

    @Getter
    private final DeepAssertType deepAssertType;

    public Config(DeepAssertType deepAssertType) {
        this.deepAssertType = deepAssertType;
    }

    public void setDeepAssertTags(String... deepAssertTags) {
        this.deepAssertTags = List.of(deepAssertTags);
    }

    public void setDeepVerifiablePackages(String... deepVerifiablePackages) {
        this.deepVerifiablePackages = List.of(deepVerifiablePackages);
    }
}
