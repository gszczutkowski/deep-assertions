package com.testcraftsmanship.deepassertions.core.api;

import com.testcraftsmanship.deepassertions.core.config.Config;
import org.junit.jupiter.api.BeforeAll;

public class DeepComparatorDefinedTest {
    private static Config config;

    @BeforeAll
    public static void setUp() {
        config = new Config();
        config.setDeepVerifiablePackages("com.testcraftsmanship");
        config.setWithAnyOrder(false);
    }
}
