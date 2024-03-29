package com.testcraftsmanship.deepassertions.core.performance;

import com.testcraftsmanship.deepassertions.core.annotations.DeepVerifiable;
import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Tag("performance")
public class PerformanceTest extends BasePerformanceTest {
    private static final int SIZE = 1000;


    @Test
    public void performanceTest() {
        Parent parentA = new Parent();
        Parent parentB = new Parent();
        parentB.setDiff(10);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 40; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions.assertThat(parentA).isEqualTo(parentB);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (4 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(70000);
    }

    @DeepVerifiable
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


    @DeepVerifiable
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
