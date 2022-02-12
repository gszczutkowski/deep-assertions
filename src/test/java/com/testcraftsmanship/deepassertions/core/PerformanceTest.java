package com.testcraftsmanship.deepassertions.core;

import com.testcraftsmanship.deepassertions.core.annotations.Verifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Tag("performance")
public class PerformanceTest {
    private static final int SIZE = 1000;

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
        parentB.setDiff(10);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions.assertThat(parentA).isEqualTo(parentB);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (4 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(60000);
    }

    @Verifiable
    static class ChildA {
        List<String> names;
        Map<String, Item> nameToItem;
        Set<String> uniques;

        public ChildA() {
            this.names = namesGenerator(SIZE);
            this.nameToItem = nameToItemGenerator(SIZE);
            this.uniques = new HashSet<>(namesGenerator(SIZE));
        }
    }

    @Verifiable
    @EqualsAndHashCode
    static class Item {
        private final String color;
        private final int value;

        public Item() {
            this.color = "Red";
            this.value = SIZE;
        }
    }

    @Verifiable
    class Parent {
        int[] numbers1;
        int[] numbers2;
        List<ChildA> children;
        Set<String> texts;

        public Parent() {
            this.numbers1 = numbersGenerator(SIZE);
            this.numbers2 = numbersGenerator(SIZE);
            this.children = childrenGenerator(SIZE);
            this.texts = new HashSet<>(namesGenerator(SIZE));
        }

        public void setDiff(int position) {
            numbers1[position] = 0;
            children.get(position).names.set(position, "Changed");
            if (!texts.isEmpty())
                texts.remove(texts.iterator().next());
        }
    }
}
