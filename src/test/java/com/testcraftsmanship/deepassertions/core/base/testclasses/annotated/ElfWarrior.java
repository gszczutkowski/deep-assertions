package com.testcraftsmanship.deepassertions.core.base.testclasses.annotated;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;


public class ElfWarrior extends Elf {
    @DeepVerifiable
    private Weapon weapon;

    public ElfWarrior(String firstName, String lastName, Weapon weapon) {
        super(firstName, lastName);
        this.weapon = weapon;
    }


    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " with " + weapon.getName();
    }
}
