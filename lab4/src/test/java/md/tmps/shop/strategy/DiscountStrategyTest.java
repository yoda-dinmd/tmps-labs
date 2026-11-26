package md.tmps.shop.strategy;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountStrategyTest {
    private final Product keyboard = new Product("P1", "Mechanical keyboard", new BigDecimal("850.00"));
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));

    @Test
    void eachStrategyProducesExpectedDiscount() {
        DiscountStrategy none = new NoDiscountStrategy();
        DiscountStrategy tenPercent = new PercentageDiscountStrategy(new BigDecimal("10"));
        DiscountStrategy threshold = new ThresholdFixedDiscountStrategy(
                new BigDecimal("500.00"), new BigDecimal("100.00"));

        assertEquals(new BigDecimal("0.00"), none.discountFor(new BigDecimal("850.00")));
        assertEquals(new BigDecimal("85.00"), tenPercent.discountFor(new BigDecimal("850.00")));
        assertEquals(new BigDecimal("0.00"), threshold.discountFor(new BigDecimal("240.00")));
        assertEquals(new BigDecimal("100.00"), threshold.discountFor(new BigDecimal("850.00")));
    }

    @Test
    void checkoutSwapsStrategiesAtRuntimeAndSupportsNewImplementations() {
        Cart cart = new Cart();
        cart.add(keyboard, 1);
        cart.add(cable, 1);
        // Subtotal = 970.00 MDL

        CheckoutService checkout = new CheckoutService(new NoDiscountStrategy());
        Order regularOrder = checkout.checkout(cart);
        assertEquals(new BigDecimal("970.00"), regularOrder.total());

        checkout.setDiscountStrategy(new PercentageDiscountStrategy(new BigDecimal("10")));
        Order percentageOrder = checkout.checkout(cart);
        assertEquals(new BigDecimal("97.00"), percentageOrder.discount());
        assertEquals(new BigDecimal("873.00"), percentageOrder.total());

        checkout.setDiscountStrategy(new ThresholdFixedDiscountStrategy(
                new BigDecimal("500.00"), new BigDecimal("100.00")));
        Order thresholdOrder = checkout.checkout(cart);
        assertEquals(new BigDecimal("100.00"), thresholdOrder.discount());
        assertEquals(new BigDecimal("870.00"), thresholdOrder.total());

        // Open for extension: a new strategy plugs in without modifying CheckoutService.
        DiscountStrategy flatFifty = new DiscountStrategy() {
            @Override
            public String description() {
                return "50 MDL flat off";
            }

            @Override
            public BigDecimal discountFor(BigDecimal subtotal) {
                return subtotal.min(new BigDecimal("50.00"));
            }
        };
        checkout.setDiscountStrategy(flatFifty);
        Order customOrder = checkout.checkout(cart);
        assertEquals(new BigDecimal("50.00"), customOrder.discount());
        assertEquals(new BigDecimal("920.00"), customOrder.total());
    }
}
