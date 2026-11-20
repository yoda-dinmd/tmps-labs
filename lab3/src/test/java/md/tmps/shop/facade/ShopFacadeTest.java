package md.tmps.shop.facade;

import md.tmps.shop.catalog.InMemoryProductCatalog;
import md.tmps.shop.checkout.CheckoutService;
import md.tmps.shop.checkout.DiscountPolicy;
import md.tmps.shop.checkout.NoDiscount;
import md.tmps.shop.checkout.RegularCheckoutService;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopFacadeTest {
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));
    private final InMemoryProductCatalog catalog = InMemoryProductCatalog.getInstance();

    ShopFacadeTest() {
        catalog.save(cable);
    }

    @Test
    void calculatesAndPrintsBeforeClearingTheCart() {
        List<String> calls = new ArrayList<>();
        CheckoutService checkout = new CheckoutService() {
            @Override
            protected DiscountPolicy createDiscountPolicy() {
                calls.add("calculate");
                return new NoDiscount();
            }
        };
        Shop shop = new ShopFacade(catalog, checkout, (order, out) -> {
            calls.add("print");
            assertEquals(new BigDecimal("240.00"), order.total());
        });
        shop.addToCart("P3", 2);

        shop.checkout(System.out);

        assertEquals(List.of("calculate", "print"), calls);
        assertTrue(shop.cartItems().isEmpty());
    }

    @Test
    void leavesCartIntactWhenReceiptPrintingFails() {
        IllegalStateException failure = new IllegalStateException("Receipt failed");
        Shop shop = new ShopFacade(catalog, new RegularCheckoutService(), (order, out) -> {
            throw failure;
        });
        shop.addToCart("P3", 2);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> shop.checkout(System.out)));
        assertEquals(List.of(new CartItem(cable, 2)), shop.cartItems());
    }
}
