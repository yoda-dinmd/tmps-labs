package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrderEventBus implements OrderSubject {
    private final List<OrderObserver> observers = new ArrayList<>();

    @Override
    public void subscribe(OrderObserver observer) {
        Objects.requireNonNull(observer, "observer");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean isSubscribed(OrderObserver observer) {
        return observers.contains(observer);
    }

    @Override
    public List<OrderObserver> observers() {
        return List.copyOf(observers);
    }

    @Override
    public void notifyObservers(Order order) {
        Objects.requireNonNull(order, "order");
        for (OrderObserver observer : List.copyOf(observers)) {
            observer.onOrderCompleted(order);
        }
    }
}
