package md.tmps.shop.facade;

import md.tmps.shop.catalog.ProductCatalog;
import md.tmps.shop.command.AddToCartCommand;
import md.tmps.shop.command.CommandHistory;
import md.tmps.shop.command.RemoveFromCartCommand;
import md.tmps.shop.command.SetDiscountStrategyCommand;
import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import md.tmps.shop.observer.OrderObserver;
import md.tmps.shop.observer.OrderSubject;
import md.tmps.shop.receipt.Receipt;
import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.DiscountStrategy;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ShopFacade implements Shop {
    private final ProductCatalog catalog;
    private final CheckoutService checkout;
    private final Receipt receipt;
    private final CommandHistory history;
    private final OrderSubject orderSubject;
    private final Cart cart = new Cart();

    public ShopFacade(ProductCatalog catalog,
                      CheckoutService checkout,
                      Receipt receipt,
                      CommandHistory history,
                      OrderSubject orderSubject) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.checkout = Objects.requireNonNull(checkout, "checkout");
        this.receipt = Objects.requireNonNull(receipt, "receipt");
        this.history = Objects.requireNonNull(history, "history");
        this.orderSubject = Objects.requireNonNull(orderSubject, "orderSubject");
    }

    @Override
    public List<Product> products() {
        return catalog.findAll();
    }

    @Override
    public void addToCart(String productId, int quantity) {
        Product product = catalog.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product ID."));
        history.execute(new AddToCartCommand(cart, product, quantity));
    }

    @Override
    public List<CartItem> cartItems() {
        return cart.items();
    }

    @Override
    public boolean removeFromCart(String productId, int quantity) {
        return history.execute(new RemoveFromCartCommand(cart, productId, quantity));
    }

    @Override
    public DiscountStrategy activeDiscount() {
        return checkout.discountStrategy();
    }

    @Override
    public void setDiscountStrategy(DiscountStrategy strategy) {
        history.execute(new SetDiscountStrategyCommand(checkout, strategy));
    }

    @Override
    public Optional<String> undoLastAction() {
        return history.undo();
    }

    @Override
    public List<String> actionHistory() {
        return history.descriptions();
    }

    @Override
    public void subscribeObserver(OrderObserver observer) {
        orderSubject.subscribe(observer);
    }

    @Override
    public void unsubscribeObserver(OrderObserver observer) {
        orderSubject.unsubscribe(observer);
    }

    @Override
    public boolean isObserverSubscribed(OrderObserver observer) {
        return orderSubject.isSubscribed(observer);
    }

    @Override
    public Order checkout(PrintStream out) {
        Order order = checkout.checkout(cart);
        receipt.print(order, out);
        cart.clear();
        history.clear();
        orderSubject.notifyObservers(order);
        return order;
    }
}
