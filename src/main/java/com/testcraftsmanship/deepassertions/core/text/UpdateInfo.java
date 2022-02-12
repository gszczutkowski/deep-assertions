package com.testcraftsmanship.deepassertions.core.text;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UpdateInfo {
    private Class realClass;
    private CheckType checkType;
    @Setter
    private Object numbersValidationKey;
    @Setter
    private Class itemClass;

    public UpdateInfo(Class realClass) {
        this.realClass = realClass;
        this.checkType = CheckType.DEFAULT;
    }

    public void setCollectionDuplicatesIfNotSet(Class collectionClass, Class collectionItemClass) {
        checkType = CheckType.COLLECTION_DUPLICATES;
        this.realClass = collectionClass;
        this.itemClass = collectionItemClass;
    }
}
