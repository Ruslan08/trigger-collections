package dev.rus4j.collect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TriggerCollection implements InvocationHandler {
    private static TriggerCollectionBuilder builder;

    protected TriggerCollection() {
    }

    /**
     * Create trigger builder that can be used for adding new triggers to collection.
     * Use {@link TriggerCollectionBuilder#build()} to build trigger collection.
     *
     * @param backedList original collection
     * @param <E>        type of original collection
     * @param <T>        TriggeredCollectionBuilder subclass
     * @return           {@link TriggerCollectionBuilder} to add triggers
     */
    @SuppressWarnings("unchecked")
    public static <E, T extends TriggerCollectionBuilder<E, T>> TriggerCollectionBuilder<E, T> from(
        Collection<E> backedList
    ) {
        builder = new TriggerCollectionBuilder<>(backedList);
        return (TriggerCollectionBuilder<E, T>) builder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("add")) {
            builder.beforeAdd.accept(args[0]);
            final Object result = method.invoke(builder.backedCollection, args);
            builder.afterAdd.accept(args[0], result);
            return result;
        }
        if (method.getName().equals("remove")) {
            builder.beforeRemove.accept(args[0]);
            final Object result = method.invoke(builder.backedCollection, args);
            builder.afterRemove.accept(args[0], result);
            return result;
        }
        if (method.getName().equals("addAll")) {
            builder.beforeAddAll.accept(args[0]);
            final Object result = method.invoke(builder.backedCollection, args);
            builder.afterAddAll.accept(args[0], result);
            return result;
        }
        if (method.getName().equals("removeAll")) {
            builder.beforeRemoveAll.accept(args[0]);
            final Object result = method.invoke(builder.backedCollection, args);
            builder.afterRemoveAll.accept(args[0], result);
            return result;
        }
        return method.invoke(builder.backedCollection, args);
    }

    public static class TriggerCollectionBuilder<E, T extends TriggerCollectionBuilder<E, T>> {

        private Collection<E> backedCollection;

        private Consumer<E> beforeAdd = valueToAdd -> {};
        private BiConsumer<E, Boolean> afterAdd = (valueToAdd, result) -> {};

        private Consumer<Collection<E>> beforeAddAll = valueToAdd -> {};
        private BiConsumer<Collection<E>, Boolean> afterAddAll = (valueToAdd, result) -> {};

        private Consumer<E> beforeRemove = valueToRemove -> {};
        private BiConsumer<E, Boolean> afterRemove = (valueToRemove, result) -> {};

        private Consumer<Collection<E>> beforeRemoveAll = valueToRemove -> {};
        private BiConsumer<Collection<E>, Boolean> afterRemoveAll = (valueToRemove, result) -> {};

        TriggerCollectionBuilder(Collection<E> backedCollection) {
            if (builder == null) {
                builder = this;
            }
            this.backedCollection = backedCollection;
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T)this;
        }

        /**
         * Adds action that will be executed before {@link Collection#add(Object)} method is called.
         * @param beforeAdd action with element to be added as a parameter
         * @return builder
         */
        public T beforeAdd(Consumer<E> beforeAdd) {
            this.beforeAdd = beforeAdd;
            return self();
        }

        /**
         * Adds action that will be executed after {@link Collection#add(Object)} method is called.
         * @param afterAdd action with element to be added and result as parameters
         * @return builder
         */
        public T afterAdd(BiConsumer<E, Boolean> afterAdd) {
            this.afterAdd = afterAdd;
            return self();
        }

        /**
         * Adds action that will be executed before {@link Collection#addAll(Collection)} method is called.
         * @param beforeAddAll action with collection of elements to be added as a parameter
         * @return builder
         */
        public T beforeAddAll(Consumer<Collection<E>> beforeAddAll) {
            this.beforeAddAll = beforeAddAll;
            return self();
        }

        /**
         * Adds action that will be executed after {@link Collection#addAll(Collection)} method is called.
         * @param afterAddAll action with collection of elements to be added and result as parameters
         * @return builder
         */
        public T afterAddAll(BiConsumer<Collection<E>, Boolean> afterAddAll) {
            this.afterAddAll = afterAddAll;
            return self();
        }

        /**
         * Adds action that will be executed before {@link Collection#remove(Object)} method is called.
         * @param beforeRemove action with element to be removed as a parameter
         * @return builder
         */
        public T beforeRemove(Consumer<E> beforeRemove) {
            this.beforeRemove = beforeRemove;
            return self();
        }

        /**
         * Adds action that will be executed after {@link Collection#remove(Object)} method is called.
         * @param afterRemove action with element to be removed and result as parameters
         * @return builder
         */
        public T afterRemove(BiConsumer<E, Boolean> afterRemove) {
            this.afterRemove = afterRemove;
            return self();
        }


        /**
         * Adds action that will be executed before {@link Collection#removeAll(Collection)} method is called.
         * @param beforeRemoveAll action with collection of elements to be removed as a parameter
         * @return builder
         */
        public T beforeRemoveAll(Consumer<Collection<E>> beforeRemoveAll) {
            this.beforeRemoveAll = beforeRemoveAll;
            return self();
        }

        /**
         * Adds action that will be executed after {@link Collection#removeAll(Collection)} method is called.
         * @param afterRemoveAll action with collection of elements to be removed and result as parameters
         * @return builder
         */
        public T afterRemoveAll(BiConsumer<Collection<E>, Boolean> afterRemoveAll) {
            this.afterRemoveAll = afterRemoveAll;
            return self();
        }

        /**
         * Creates a proxy instance.
         * @return proxy instance for the {@link Collection}
         */
        @SuppressWarnings("unchecked")
        public Collection<E> build() {
            return (Collection<E>) Proxy.newProxyInstance(
                    backedCollection.getClass().getClassLoader(),
                    backedCollection.getClass().getInterfaces(),
                    new TriggerCollection()
            );
        }
    }
}
