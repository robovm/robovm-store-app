package org.robovm.store.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Basket implements Iterable<Order> {
    private final List<Order> orders = new ArrayList<>();
    private final List<Runnable> basketChangeListeners = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;
    }

    public void add(Order order) {
        orders.add(order);
        onBasketChange();
    }

    public Order get(int index) {
        return orders.get(index);
    }

    public Order remove(int index) {
        Order old = orders.remove(index);
        onBasketChange();
        return old;
    }

    public void clear() {
        orders.clear();
        onBasketChange();
    }

    public int size() {
        return orders.size();
    }

    protected void onBasketChange() {
        for (Runnable r : basketChangeListeners) {
            r.run();
        }
    }

    @Override
    public Iterator<Order> iterator() {
        return orders.iterator();
    }

    public void addOnBasketChangeListener(Runnable listener) {
        basketChangeListeners.add(listener);
    }

    public void removeOnBasketChangeListener(Runnable listener) {
        basketChangeListeners.remove(listener);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Basket\n");
        for (Order order : orders) {
            sb.append(order.toString()).append("\n");
        }
        return sb.toString();
    }
}
