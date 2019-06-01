package dev.rus4j.collect;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TriggerCollectionTest {
    private static final int VALUE_TO_ADD = 7;
    private Collection<Integer> collection;

    @Before
    public void setUp() {
        this.collection = new ArrayList<>();
    }

    @Test
    public void testAfterAddTrigger() {
        BiConsumer<Integer, Boolean> testConsumer = (i, bool) -> {
            assertEquals(Integer.valueOf(VALUE_TO_ADD), i);
            assertTrue(bool);
        };

        Collection<Integer> triggered = TriggerCollection.from(this.collection)
                .afterAdd(testConsumer)
                .build();

        @SuppressWarnings("unused")
        boolean added = triggered.add(VALUE_TO_ADD);
    }

    @Test
    public void testBeforeAddTrigger() {
        Consumer<Integer> testConsumer = i -> assertEquals(Integer.valueOf(VALUE_TO_ADD), i);
        Collection<Integer> triggered = TriggerCollection.from(this.collection)
                .beforeAdd(testConsumer)
                .build();

        @SuppressWarnings("unused")
        boolean added = triggered.add(VALUE_TO_ADD);
    }
}
