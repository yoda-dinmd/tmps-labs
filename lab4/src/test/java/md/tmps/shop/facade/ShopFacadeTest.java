package md.tmps.shop.facade;

import md.tmps.shop.catalog.InMemoryProductCatalog;
import md.tmps.shop.command.CommandHistory;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import md.tmps.shop.observer.LoyaltyPointsObserver;
import md.tmps.shop.observer.OrderAuditObserver;
import md.tmps.shop.observer.OrderEventBus;
import md.tmps.shop.receipt.ItemCountReceipt;
import md.tmps.shop.receipt.ReceiptPrinter;
import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.DiscountStrategy;
import md.tmps.shop.strategy.NoDiscountStrategy;
import md.tmps.shop.strategy.ThresholdFixedDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopFacadeTest {
    private final Product keyboard = new Product("P1", "Mechanical keyboard", new BigDecimal("850.00"));
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));
    private final InMemoryProductCatalog catalog = InMemoryProductCatalog.getInstance();

    ShopFacadeTest() {
        catalog.save(keyboard);
        catalog.save(cable);
    }

    @Test
    void calculatesPrintsClearsCartAndNotifiesObserversInOrder() {
        List<String> calls = new ArrayList<>();
        DiscountStrategy recordingStrategy = new DiscountStrategy() {
            @Override
            public String description() {
                return "Recording";
            }

            @Override
            public BigDecimal discountFor(BigDecimal subtotal) {
                calls.add("calculate");
                return new BigDecimal("0.00");
            }
        };
        OrderEventBus eventBus = new OrderEventBus();
        eventBus.subscribe(new md.tmps.shop.observer.OrderObserver() {
            @Override
            public String name() {
                return "Recorder";
            }

            @Override
            public void onOrderCompleted(Order order) {
                calls.add("notify");
            }
        });
        Shop shop = new ShopFacade(
                catalog,
                new CheckoutService(recordingStrategy),
                (order, out) -> {
                    calls.add("print");
                    assertEquals(new BigDecimal("240.00"), order.total());
                },
                new CommandHistory(),
                eventBus);
        shop.addToCart("P3", 2);

        shop.checkout(System.out);

        assertEquals(List.of("calculate", "print", "notify"), calls);
        assertTrue(shop.cartItems().isEmpty());
        assertTrue(shop.actionHistory().isEmpty());
    }

    @Test
    void leavesCartIntactAndSkipsObserversWhenReceiptPrintingFails() {
        IllegalStateException failure = new IllegalStateException("Receipt failed");
        OrderEventBus eventBus = new OrderEventBus();
        OrderAuditObserver audit = new OrderAuditObserver(silentStream());
        eventBus.subscribe(audit);

        Shop shop = new ShopFacade(
                catalog,
                new CheckoutService(new NoDiscountStrategy()),
                (order, out) -> {
                    throw failure;
                },
                new CommandHistory(),
                eventBus);
        shop.addToCart("P3", 2);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> shop.checkout(System.out)));
        assertEquals(List.of(new CartItem(cable, 2)), shop.cartItems());
        assertTrue(audit.recordedOrders().isEmpty());
    }

    @Test
    void commandStrategyAndObserverCooperateAcrossCheckoutFlow() {
        OrderEventBus eventBus = new OrderEventBus();
        LoyaltyPointsObserver loyalty = new LoyaltyPointsObserver(silentStream());
        OrderAuditObserver audit = new OrderAuditObserver(silentStream());
        eventBus.subscribe(loyalty);
        eventBus.subscribe(audit);

        Shop shop = new ShopFacade(
                catalog,
                new CheckoutService(new NoDiscountStrategy()),
                new ItemCountReceipt(new ReceiptPrinter()),
                new CommandHistory(),
                eventBus);

        shop.addToCart("P1", 1);
        shop.addToCart("P3", 2);
        shop.undoLastAction();
        shop.setDiscountStrategy(new ThresholdFixedDiscountStrategy(
                new BigDecimal("500.00"), new BigDecimal("100.00")));

        Order order = shop.checkout(silentStream());

        assertEquals(new BigDecimal("850.00"), order.subtotal());
        assertEquals(new BigDecimal("100.00"), order.discount());
        assertEquals(new BigDecimal("750.00"), order.total());
        assertEquals(75, loyalty.totalPoints());
        assertEquals(1, audit.recordedOrders().size());
        assertEquals(order, audit.recordedOrders().get(0));
    }

    private PrintStream silentStream() {
        return new PrintStream(new ByteArrayOutputStream(), false, StandardCharsets.UTF_8);
    }
}
