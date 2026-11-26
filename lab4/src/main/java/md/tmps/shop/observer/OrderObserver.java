package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

// Observer: reacts to completed orders without coupling the publisher to concrete listeners.
public interface OrderObserver {
    String name();
    void onOrderCompleted(Order order);
}
