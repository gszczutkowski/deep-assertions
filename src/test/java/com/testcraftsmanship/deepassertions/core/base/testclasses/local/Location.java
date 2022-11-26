package com.testcraftsmanship.deepassertions.core.base.testclasses.local;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class Location {
    private String city;
    private String street;
    private int streetNumber;
    private Integer flatNumber;
    private String[] tags;
    private int[] numbers;
    private Set<String> roomNames;
    private List<Building> buildings;
    private UUID uuid;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Building {
        private String buildingName;
        private int allRoomsNumber;
        private Map<Integer, Integer> roomsPerFlor;
    }
}
