package com.testcraftsmanship.deepassertions.core.base.testclasses.local;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Druid {
    private String name;
    @DeepVerifiable
    private Staff staff;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Druid druid = (Druid) o;
        return name.equals(druid.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
