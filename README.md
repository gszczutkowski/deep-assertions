# What is deep-assertions
[![continuous-integration](https://github.com/gszczutkowski/deep-assertions/actions/workflows/continuous-integration.yml/badge.svg)](https://github.com/gszczutkowski/deep-assertions/actions/workflows/continuous-integration.yml)

The deep-assertions allows you to perform assertions against two objects. Tool does not use the equals() method on the object but uses reflections to compare values of the fields in given object. 
It is recursive operation. We can have rules defining which fields we want to include in the comparison and which fields we want
to compare deeply (so compare fields of the objects).The result of the assertion indicates all fields which differs and adds information about the field type and values.
# How to use it
Let's assume that we have class Mage and Staff in package com.testcraftsmanship and we want to performa assertion on two objects as below. With use of method withPackages() we indicate that for all classes
from this package we want to perform deep assertion.
```java
class Mage {
    private String name;
    private int spellPower;
    private Staff staff;
}

class Staff {
    private boolean twoHanded;
    private int power;
}

...

Mage actualMage = new Mage("Gandalf", 10, new Staff(true, 5));
Mage expectedMage = new Mage("Dumbledore", 10, new Staff(false, 5));

DeepAssertions
        .assertThat(actualMage)
        .withPackages("com.testcraftsmanship")
        .isEqualTo(expectedMage);
```

As a result of this assertion we can see the listing as below. String is not from package com.testcraftsmanship so deep assertion was not taken into consideration
but method equals was used to check the values. As Staff class is from the package we performed deep assertion and equals was performed on field of the Staff class.
There are two differences between those two objects and both were indicated in the assertion result.
```bash
Multiple Failures (2 failures)
-- failure 1 --Mage.name<String> - actual object has value {Gandalf}, expect to have {Dumbledore}
-- failure 2 --Mage.staff.twoHanded<Boolean> - actual object has value {true}, expect to have {false}
```

