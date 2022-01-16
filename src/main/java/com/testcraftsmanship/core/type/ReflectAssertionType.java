package com.testcraftsmanship.core.type;

import lombok.Getter;

public enum ReflectAssertionType {
    USER("user"), ITEM("organization");
    @Getter
    private String type;

    ReflectAssertionType(String type) {
        this.type = type;
    }
}
