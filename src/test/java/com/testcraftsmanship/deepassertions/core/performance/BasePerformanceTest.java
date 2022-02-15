package com.testcraftsmanship.deepassertions.core.performance;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePerformanceTest {
    protected static List<String> namesGenerator(int size) {
        String temp = "Extended kindness trifling remember he confined outlived if. Assistance sentiments yet unpleasing say. Open they an busy they my such high. An active dinner wishes at unable hardly no talked on. Immediate him her resolving his favourite. Wished denote abroad at branch at.";
        List<String> names = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            names.add(temp + i);
        }
        return names;
    }

    protected static String namesOnPosition(int position) {
        return  "Extended kindness trifling remember he confined outlived if. Assistance sentiments yet unpleasing say. Open they an busy they my such high. An active dinner wishes at unable hardly no talked on. Immediate him her resolving his favourite. Wished denote abroad at branch at."
                + position;
    }

    protected static Map<String, PerformanceTest.Item> nameToItemGenerator(int size) {
        String temp = "Extended kindness trifling remember he confined outlived if. Assistance sentiments yet unpleasing say. Open they an busy they my such high. An active dinner wishes at unable hardly no talked on. Immediate him her resolving his favourite. Wished denote abroad at branch at.";
        Map<String, PerformanceTest.Item> names = new HashMap<>();
        for (int i = 0; i < size; i++) {
            names.put(temp + i, new PerformanceTest.Item());
        }
        return names;
    }

    protected static List<PerformanceTest.ChildA> childrenGenerator(int size) {
        List<PerformanceTest.ChildA> children = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            children.add(new PerformanceTest.ChildA());
        }
        return children;
    }

    protected static int[] numbersGenerator(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i;
        }
        return numbers;
    }

    @Verifiable
    @EqualsAndHashCode
    static class Item {
        private final String color;
        private final int value;

        public Item() {
            this.color = "Red";
            this.value = 100;
        }

        public Item(String color) {
            this.color = color;
            this.value = 100;
        }
    }
}
