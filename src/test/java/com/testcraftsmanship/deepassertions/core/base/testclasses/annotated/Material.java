package com.testcraftsmanship.deepassertions.core.base.testclasses.annotated;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Material {
    private String name;
    private int weight;

    public String toString() {
        return name + " (" + weight + " kg)";
    }
}
