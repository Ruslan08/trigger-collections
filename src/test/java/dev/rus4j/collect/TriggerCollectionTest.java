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
        assertEquals(1, collection.size());
    }

    @Test
    public void testBeforeAddTrigger() {
        Consumer<Integer> testConsumer = i -> assertEquals(Integer.valueOf(VALUE_TO_ADD), i);
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeAdd(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
        assertEquals(1, collection.size());
    }

    @Test
    public void testBeforeAddAllTrigger() {
        Consumer<Collection<Integer>> testConsumer = collectionToAdd -> assertEquals(LIST_TO_ADD.size(), collectionToAdd.size());
        Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeAddAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
        assertEquals(3, collection.size());
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
        assertEquals(3, collection.size());
    }

    @Test
    public void testBeforeRemoveTrigger() {
        Consumer<Integer> testConsumer = valueToRemove -> assertEquals(Integer.valueOf(VALUE_TO_ADD), valueToRemove);
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeRemove(testConsumer)
            .build();

        collection.add(VALUE_TO_ADD);
        collection.remove(VALUE_TO_ADD);
        assertEquals(0, collection.size());
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
        assertEquals(0, collection.size());
    }

    @Test
    public void testBeforeRemoveAllTrigger() {
        Consumer<Collection<Integer>> testConsumer = collectionToRemove -> assertEquals(2, collectionToRemove.size());
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .beforeRemoveAll(testConsumer)
            .build();

        collection.addAll(LIST_TO_ADD);
        collection.removeAll(LIST_TO_REMOVE);
        assertEquals(1, collection.size());
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
        assertEquals(1, collection.size());
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

    @Test
    public void testAllowAllAll() {
        Predicate<List<Integer>> predicate = list -> list.stream().allMatch(i -> i < 5);
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .allowAddAll(predicate)
            .build();

        collection.addAll(List.of(1, 2, 3, 4, 5));
        assertEquals(0, collection.size());
    }

    @Test
    public void testAllowRemove() {
        Predicate<Integer> predicate = integer -> integer <= 5;
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .allowRemove(predicate)
            .build();

        collection.addAll(List.of(1, 2, 3, 4, 5, 6));
        collection.remove(1);
        collection.remove(6);
        assertEquals(5, collection.size());
    }

    @Test
    public void testAllowRemoveAll() {
        Predicate<List<Integer>> predicate = list -> list.stream().allMatch(i -> i < 5);
        final Collection<Integer> collection = TriggerCollection.from(this.collection)
            .allowRemoveAll(predicate)
            .build();

        collection.addAll(List.of(1, 2, 3, 4, 5, 6, 7));
        collection.removeAll(List.of(1, 3, 5, 7));
        assertEquals(7, collection.size());
    }
}
