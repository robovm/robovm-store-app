package org.robovm.store.util;

public interface Action2<A, B> {
    void invoke(A a, B b);
}
