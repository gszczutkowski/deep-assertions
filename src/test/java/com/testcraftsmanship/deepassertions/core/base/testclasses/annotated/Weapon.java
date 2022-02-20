package com.testcraftsmanship.deepassertions.core.base.testclasses.annotated;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Weapon {
    private String name;
    private int distance;
    private int attackPower;
    private Material material;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weapon weapon = (Weapon) o;
        return attackPower == weapon.attackPower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attackPower);
    }
}
