package md.tmps.shop.facade;

import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import md.tmps.shop.observer.OrderObserver;
import md.tmps.shop.strategy.DiscountStrategy;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public interface Shop {
    List<Product> products();
    void addToCart(String productId, int quantity);
    List<CartItem> cartItems();
    boolean removeFromCart(String productId, int quantity);
    DiscountStrategy activeDiscount();
    void setDiscountStrategy(DiscountStrategy strategy);
    Optional<String> undoLastAction();
    List<String> actionHistory();
    void subscribeObserver(OrderObserver observer);
    void unsubscribeObserver(OrderObserver observer);
    boolean isObserverSubscribed(OrderObserver observer);
    Order checkout(PrintStream out);
}
