package dev.rus4j.collect;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TriggerCollectionTest {
    private static final int VALUE_TO_ADD = 7;
    private static final List<Integer> LIST_TO_ADD = List.of(1, 2, 3);
    private static final List<Integer> LIST_TO_REMOVE = List.of(2, 3);
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
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .afterAdd(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
    }

    @Test
    public void testBeforeAddTrigger() {
        Consumer<Integer> testConsumer = i -> assertEquals(Integer.valueOf(VALUE_TO_ADD), i);
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeAdd(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
    }

    @Test
    public void testBeforeAddAllTrigger() {
        Consumer<Collection<Integer>> testConsumer = collectionToAdd -> assertEquals(LIST_TO_ADD.size(), collectionToAdd.size());
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeAddAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
    }

    @Test
    public void testAfterAddAllTrigger() {
        BiConsumer<Collection<Integer>, Boolean> testConsumer = (collectionToAdd, result) -> {
            assertEquals(LIST_TO_ADD.size(), collectionToAdd.size());
            assertTrue(result);
        };
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .afterAddAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
    }

    @Test
    public void testBeforeRemoveTrigger() {
        Consumer<Integer> testConsumer = valueToRemove -> assertEquals(Integer.valueOf(VALUE_TO_ADD), valueToRemove);
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeRemove(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
        collection.remove(VALUE_TO_ADD);
    }

    @Test
    public void testAfterRemoveTrigger() {
        BiConsumer<Integer, Boolean> testConsumer = (valueToRemove, result) -> {
            assertEquals(Integer.valueOf(VALUE_TO_ADD), valueToRemove);
            assertTrue(result);
        };
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .afterRemove(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
        collection.remove(VALUE_TO_ADD);
    }

    @Test
    public void testBeforeRemoveAllTrigger() {
        Consumer<Collection<Integer>> testConsumer = collectionToRemove -> assertEquals(2, collectionToRemove.size());
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeRemoveAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
        collection.removeAll(LIST_TO_REMOVE);
    }

    @Test
    public void testAfterRemoveAllTrigger() {
        BiConsumer<Collection<Integer>, Boolean> testConsumer = (collectionToRemove, result) -> {
            assertEquals(2, collectionToRemove.size());
            assertTrue(result);
        };
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .afterRemoveAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
        collection.removeAll(LIST_TO_REMOVE);
    }

    @Test
    public void testAllowAdd() {
        Predicate<Integer> predicate = integer -> integer <= 5;

        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .allowAdd(predicate)
            .build();

        collection.add(5);
        collection.add(6);

        assertEquals(1, collection.size());
    }
}
