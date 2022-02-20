package com.testcraftsmanship.deepassertions.core.base.testclasses.local;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Mage {
    private String name;
    private int spellPower;
    private Staff staff;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mage mage = (Mage) o;
        return spellPower == mage.spellPower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spellPower);
    }
}
