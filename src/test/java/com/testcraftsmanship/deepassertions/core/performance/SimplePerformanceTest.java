package com.testcraftsmanship.deepassertions.core.performance;

import com.testcraftsmanship.deepassertions.core.api.DeepAssertions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Tag("performance")
public class SimplePerformanceTest extends BasePerformanceTest {

    @Test
    public void checkMapPerformanceInAnyOrder() {
        Item item = new Item("Black");
        Map<String, Item> actualMap = nameToItemGenerator(10000);
        Map<String, Item> expectedMap = nameToItemGenerator(9999);
        actualMap.put(namesOnPosition(4000), item);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions
                        .assertThat(actualMap)
                        .withAnyOrder()
                        .isEqualTo(expectedMap);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (3 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Map assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(14000);
    }

    @Test
    public void checkArrayPerformanceInAnyOrder() {
        String[] actualArray = namesGenerator(10000).toArray(new String[10000]);
        String[] expectedArray = namesGenerator(9999).toArray(new String[9999]);
        actualArray[4000] = "failed";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions
                        .assertThat(actualArray)
                        .withAnyOrder()
                        .isEqualTo(expectedArray);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (4 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Array assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(5500);
    }

    @Test
    public void checkListPerformanceInAnyOrder() {
        List<String> actualList = namesGenerator(10000);
        List<String> expectedList = namesGenerator(10000);
        actualList.remove(9999);
        actualList.set(4000, "failed");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions
                        .assertThat(actualList)
                        .withAnyOrder()
                        .isEqualTo(expectedList);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (4 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("List assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(4500);
    }

    @Test
    public void checkSetPerformanceInAnyOrder() {
        List<String> actualList = namesGenerator(10000);
        Set<String> actualSet = new HashSet<>(actualList);
        Set<String> expectedSet = new HashSet<>(namesGenerator(9999));
        actualSet.remove(actualList.get(4000));
        actualSet.add("failed");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            assertThatThrownBy(() -> {
                DeepAssertions
                        .assertThat(actualSet)
                        .withAnyOrder()
                        .isEqualTo(expectedSet);
            }).isInstanceOf(AssertionError.class)
                    .hasMessageContainingAll("Multiple Failures (4 failures)");
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Set assertion execution time: {}ms", timeElapsed);
        assertThat(timeElapsed).isLessThan(4000);
    }
}
