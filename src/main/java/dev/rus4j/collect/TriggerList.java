package dev.rus4j.collect;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TriggerList extends TriggerCollection {
    private static TriggerListBuilder builder;

    private TriggerList() {
        super();
    }

    /**
     * Create trigger builder that can be used for adding new triggers to list.
     * Use {@link TriggerListBuilder#build()} to build trigger list.
     *
     * @param backedList original collection
     * @param <T>        type of original list
     * @return           {@link TriggerListBuilder} to add triggers
     */
    @SuppressWarnings("unchecked")
    public static <T> TriggerListBuilder<T> from(List<T> backedList) {
        builder = new TriggerListBuilder<>(backedList);
        return (TriggerListBuilder<T>)builder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("get")) {
            builder.beforeGet.accept(args[0]);
            Object result = method.invoke(builder.backedList, args);
            builder.afterGet.accept(args[0], result);
            return result;
        }
        return super.invoke(proxy, method, args);
    }

    public static class TriggerListBuilder<E> extends TriggerCollectionBuilder<E, TriggerListBuilder<E>> {

        private List<E> backedList;

        private Consumer<Integer> beforeGet = t -> {};
        private BiConsumer<Integer, E> afterGet = (i, t) -> {};

        private TriggerListBuilder(List<E> backedList) {
            super(backedList);
            this.backedList = backedList;
        }

        @Override
        protected TriggerListBuilder<E> self() {
            return this;
        }

        /**
         * Adds action that will be executed before {@link List#get(int)} method is called.
         * @param beforeGet action with index of an element as a parameter
         * @return builder
         */
        public TriggerListBuilder<E> beforeGet(Consumer<Integer> beforeGet) {
            this.beforeGet = beforeGet;
            return this;
        }

        /**
         * Adds action that will be executed before {@link List#get(int)} method is called.
         * @param afterGet action with index of an element as a parameter
         * @return builder
         */
        public TriggerListBuilder<E> afterGet(BiConsumer<Integer, E> afterGet) {
            this.afterGet = afterGet;
            return this;
        }

        /**
         * Creates a proxy instance.
         * @return proxy instance for the {@link List}
         */
        @SuppressWarnings("unchecked")
        public List<E> build() {
            return (List<E>) Proxy.newProxyInstance(
                    backedList.getClass().getClassLoader(),
                    new Class[]{List.class},
                    new TriggerList()
            );
        }

    }

}
