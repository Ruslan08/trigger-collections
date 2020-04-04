package dev.rus4j.collect;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class TriggerListTest {
    private static final int VALUE_TO_GET = 7;
    private static final int INDEX_TO_GET = 0;
    private List<Integer> list;

    @Before
    public void setUp() {
        list = new ArrayList<>();
        list.add(INDEX_TO_GET, VALUE_TO_GET);
    }

    @Test
    public void testBeforeGetTrigger() {
        Consumer<Integer> testConsumer = i -> assertEquals(Integer.valueOf(INDEX_TO_GET), i);

        List<Integer> triggeredList = TriggerList.from(this.list)
                .beforeGet(testConsumer)
                .build();

        @SuppressWarnings("unused")
        Integer integer = triggeredList.get(0);
    }

    @Test
    public void testAfterGetTrigger() {
        BiConsumer<Integer, Integer> testConsumer = (arg, value) -> {
            assertEquals(Integer.valueOf(INDEX_TO_GET), arg);
            assertEquals(Integer.valueOf(VALUE_TO_GET), value);
        };

        List<Integer> triggeredList = TriggerList.from(this.list)
                .afterGet(testConsumer)
                .build();

        @SuppressWarnings("unused")
        Integer integer = triggeredList.get(0);
    }

    @Test
    public void testMethodFromCollection() {
        final Consumer<Integer> before = System.out::println;
        Consumer<List<Integer>> beforeAll = System.out::println;
        BiConsumer<Integer, Boolean> after = (i, b) -> System.out.printf("%d, %s", i, b);
        BiConsumer<List<Integer>, Boolean> afterAll = (list, b) -> System.out.printf("%d, %s", list.size(), b);

        List<Integer> triggerList = TriggerList.from(this.list)
            .beforeAdd(before)
            .beforeAddAll(beforeAll)
            .beforeRemove(before)
            .beforeRemoveAll(beforeAll)
            .afterAdd(after)
            .afterAddAll(afterAll)
            .afterRemove(after)
            .afterRemoveAll(afterAll)
            .build();
        triggerList.add(15);
    }
}
