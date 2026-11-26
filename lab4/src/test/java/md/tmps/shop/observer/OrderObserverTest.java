package md.tmps.shop.observer;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.NoDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderObserverTest {
    private final Product keyboard = new Product("P1", "Mechanical keyboard", new BigDecimal("850.00"));
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));

    @Test
    void allSubscribedObserversAreNotifiedOnOrderCompletion() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output, false, StandardCharsets.UTF_8);

        OrderEventBus bus = new OrderEventBus();
        LoyaltyPointsObserver loyalty = new LoyaltyPointsObserver(out);
        OrderAuditObserver audit = new OrderAuditObserver(out);
        HighValueOrderAlertObserver alert = new HighValueOrderAlertObserver(new BigDecimal("500.00"), out);

        bus.subscribe(loyalty);
        bus.subscribe(audit);
        bus.subscribe(alert);

        Order order = orderOf(keyboard, 1);
        bus.notifyObservers(order);

        assertEquals(85, loyalty.totalPoints());
        assertEquals(List.of(order), audit.recordedOrders());
        assertEquals(List.of(order), alert.alertedOrders());
        String log = output.toString(StandardCharsets.UTF_8);
        assertTrue(log.contains("[Loyalty] Earned 85 points"));
        assertTrue(log.contains("[Audit] Recorded order #1: 850.00 MDL"));
        assertTrue(log.contains("[Alert] High-value order: 850.00 MDL"));
    }

    @Test
    void unsubscribedObserverStopsReceivingNotifications() {
        PrintStream silent = new PrintStream(new ByteArrayOutputStream(), false, StandardCharsets.UTF_8);

        OrderEventBus bus = new OrderEventBus();
        LoyaltyPointsObserver loyalty = new LoyaltyPointsObserver(silent);
        OrderAuditObserver audit = new OrderAuditObserver(silent);

        bus.subscribe(loyalty);
        bus.subscribe(audit);
        assertTrue(bus.isSubscribed(loyalty));

        Order first = orderOf(cable, 2); // 240.00 MDL -> 24 points
        bus.notifyObservers(first);

        bus.unsubscribe(loyalty);
        assertFalse(bus.isSubscribed(loyalty));

        Order second = orderOf(keyboard, 1); // 850.00 MDL
        bus.notifyObservers(second);

        assertEquals(24, loyalty.totalPoints());
        assertEquals(List.of(first, second), audit.recordedOrders());
    }

    private Order orderOf(Product product, int quantity) {
        Cart cart = new Cart();
        cart.add(product, quantity);
        return new CheckoutService(new NoDiscountStrategy()).checkout(cart);
    }
}
