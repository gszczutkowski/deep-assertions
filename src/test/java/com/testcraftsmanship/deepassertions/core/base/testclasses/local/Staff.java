package com.testcraftsmanship.deepassertions.core.base.testclasses.local;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Staff {
    private boolean twoHanded;
    private int power;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return power == staff.power;
    }

    @Override
    public int hashCode() {
        return Objects.hash(power);
    }
}
