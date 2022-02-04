package com.testcraftsmanship.deepassertions.core.type;

import lombok.Getter;

public enum ReflectAssertionType {
    USER("user"), ITEM("item");
    @Getter
    private String type;

    ReflectAssertionType(String type) {
        this.type = type;
    }
}
