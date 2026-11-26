package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

import java.util.List;

// Subject: manages observer subscriptions and broadcasts completed orders.
public interface OrderSubject {
    void subscribe(OrderObserver observer);
    void unsubscribe(OrderObserver observer);
    boolean isSubscribed(OrderObserver observer);
    List<OrderObserver> observers();
    void notifyObservers(Order order);
}
