package com.testcraftsmanship.core;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PerformanceTest {

    private static final int SIZE = 1000;
    @Verifiable
    class Parent {
        int[] numbers;
        List<ChildA> children;

        public Parent() {
            this.numbers = numbersGenerator(SIZE);
            this.children = childrenGenerator(SIZE);
        }

        public void setDiff(int position) {
            numbers[position] = 0;
            children.get(position).names.set(position, "Changed");
        }
    }

    @Verifiable
    static class ChildA {
        List<String> names;
        Map<String, Item> nameToItem;

        public ChildA() {
            this.names = namesGenerator(SIZE);
            this.nameToItem = nameToItemGenerator(SIZE);
        }
    }

    @Verifiable
    @EqualsAndHashCode
    static class Item {
        private String color;
        private int value;

        public Item() {
            this.color = "Red";
            this.value = SIZE;
        }
    }

    private static List<String> namesGenerator(int size) {
        String temp = "Extended kindness trifling remember he confined outlived if. Assistance sentiments yet unpleasing say. Open they an busy they my such high. An active dinner wishes at unable hardly no talked on. Immediate him her resolving his favourite. Wished denote abroad at branch at.";
        List<String> names = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            names.add(temp + i);
        }
        return names;
    }

    private static Map<String, Item> nameToItemGenerator(int size) {
        String temp = "Extended kindness trifling remember he confined outlived if. Assistance sentiments yet unpleasing say. Open they an busy they my such high. An active dinner wishes at unable hardly no talked on. Immediate him her resolving his favourite. Wished denote abroad at branch at.";
        Map<String, Item> names = new HashMap<>();
        for (int i = 0; i < size; i++) {
            names.put(temp + i, new Item());
        }
        return names;
    }

    private static List<ChildA> childrenGenerator(int size) {
        List<ChildA> children = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            children.add(new ChildA());
        }
        return children;
    }

    private static int[] numbersGenerator(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i;
        }
        return numbers;
    }

    @Test
    public void performanceTest() {
        Parent parentA = new Parent();
        Parent parentB = new Parent();
        parentB.setDiff(1);

        DeepAssertions deepAssertions = new DeepAssertions();
        long start = System.currentTimeMillis();
        assertThatThrownBy(() -> {
            deepAssertions.assertEquals(parentA, parentB);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContainingAll("Multiple Failures (2 failures)");
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        assertThat(timeElapsed).isLessThan(9000);

    }
}
