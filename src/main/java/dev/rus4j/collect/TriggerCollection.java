package dev.rus4j.collect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TriggerCollection implements InvocationHandler {
    private static TriggeredCollectionBuilder builder;

    protected TriggerCollection() {}

    @SuppressWarnings("unchecked")
    public static <T> TriggeredCollectionBuilder<T> from(Collection<T> backedList) {
        builder = new TriggeredCollectionBuilder<>(backedList);
        return (TriggeredCollectionBuilder<T>) builder;
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

    static class TriggeredCollectionBuilder<T> {

        private Collection<T> backedCollection;

        private Consumer<T> beforeAdd = t -> {};
        private BiConsumer<T, Boolean> afterAdd = (t, b) -> {};

        public TriggeredCollectionBuilder(Collection<T> backedCollection) {
            if (builder == null) {
                builder = this;
            }
            this.backedCollection = backedCollection;
        }

        public TriggeredCollectionBuilder<T> beforeAdd(Consumer<T> beforeAdd) {
            this.beforeAdd = beforeAdd;
            return this;
        }

        public TriggeredCollectionBuilder<T> afterAdd(BiConsumer<T, Boolean> afterAdd) {
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
