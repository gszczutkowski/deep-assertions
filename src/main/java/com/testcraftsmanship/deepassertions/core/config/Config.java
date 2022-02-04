package com.testcraftsmanship.deepassertions.core.config;

import com.testcraftsmanship.deepassertions.core.api.DeepAssertType;
import lombok.Getter;

import java.util.List;

public class Config {
    public static final String PROJECT_PACKAGE = "com.testcraftsmanship";
    public static final List<String> DEFINED_PACKAGES = List.of("com.testcraftsmanship");

    @Getter
    private final DeepAssertType deepAssertType;

    public Config(DeepAssertType deepAssertType) {
        this.deepAssertType = deepAssertType;
    }

}
