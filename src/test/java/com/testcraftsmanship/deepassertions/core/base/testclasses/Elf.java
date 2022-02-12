package com.testcraftsmanship.deepassertions.core.base.testclasses;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Elf {
    @Verifiable
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
