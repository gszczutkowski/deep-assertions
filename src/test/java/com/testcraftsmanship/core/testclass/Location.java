package com.testcraftsmanship.core.testclass;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
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
@Verifiable(type = {"user"})
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
    @Verifiable(type = {"user"})
    public static class Building {
        private String buildingName;
        private int allRoomsNumber;
        private Map<Integer, Integer> roomsPerFlor;
    }

    public static void main(String[] args) {

    }
}
