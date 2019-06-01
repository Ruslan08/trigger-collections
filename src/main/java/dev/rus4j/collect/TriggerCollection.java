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
     * @param <T>        type of original collection
     * @return           {@link TriggerCollectionBuilder} to add triggers
     */
    @SuppressWarnings("unchecked")
    public static <T> TriggerCollectionBuilder<T> from(Collection<T> backedList) {
        builder = new TriggerCollectionBuilder<>(backedList);
        return (TriggerCollectionBuilder<T>) builder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("add")) {
            builder.beforeAdd.accept(args[0]);
            Object result = method.invoke(builder.backedCollection, args);
            builder.afterAdd.accept(args[0], result);
            return result;
        }
        return method.invoke(builder.backedCollection, args);
    }

    static class TriggerCollectionBuilder<T> {

        private Collection<T> backedCollection;

        private Consumer<T> beforeAdd = t -> {};
        private BiConsumer<T, Boolean> afterAdd = (t, b) -> {};

        public TriggerCollectionBuilder(Collection<T> backedCollection) {
            if (builder == null) {
                builder = this;
            }
            this.backedCollection = backedCollection;
        }

        public TriggerCollectionBuilder<T> beforeAdd(Consumer<T> beforeAdd) {
            this.beforeAdd = beforeAdd;
            return this;
        }

        public TriggerCollectionBuilder<T> afterAdd(BiConsumer<T, Boolean> afterAdd) {
            this.afterAdd = afterAdd;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Collection<T> build() {
            return (Collection<T>) Proxy.newProxyInstance(
                    backedCollection.getClass().getClassLoader(),
                    backedCollection.getClass().getInterfaces(),
                    new TriggerCollection()
            );
        }
    }
}
