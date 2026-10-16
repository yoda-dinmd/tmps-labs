package md.tmps.shop;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ShopTest {
    private final Product product = new Product("P1", "Keyboard", new BigDecimal("850.00"));

    @Test
    void repeatedProductsMergeAndCanBeRemoved() {
        Cart cart = new Cart();
        cart.add(product, 1);
        cart.add(product, 2);
        assertEquals(1, cart.items().size());
        assertEquals(3, cart.items().get(0).quantity());
        assertTrue(cart.remove("P1"));
        assertTrue(cart.items().isEmpty());
        assertFalse(cart.remove("missing"));
    }

    @Test
    void invalidQuantityDoesNotChangeCart() {
        Cart cart = new Cart();
        cart.add(product, 1);
        assertThrows(IllegalArgumentException.class, () -> cart.add(product, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.add(product, -1));
        assertThrows(ArithmeticException.class, () -> cart.add(product, Integer.MAX_VALUE));
        assertEquals(1, cart.items().get(0).quantity());
    }

    @Test
    void percentageCheckoutCreatesIndependentOrderSnapshot() {
        Cart cart = new Cart();
        cart.add(product, 2);
        Order order = new CheckoutService(new PercentageDiscount(new BigDecimal("10"))).checkout(cart);
        cart.clear();
        assertEquals(new BigDecimal("1700.00"), order.subtotal());
        assertEquals(new BigDecimal("170.00"), order.discount());
        assertEquals(new BigDecimal("1530.00"), order.total());
        assertEquals(2, order.items().get(0).quantity());
        assertThrows(UnsupportedOperationException.class, () -> order.items().clear());
    }

    @Test
    void newDiscountCanBeAddedWithoutChangingCheckout() {
        Cart cart = new Cart();
        cart.add(product, 1);
        DiscountPolicy fixedDiscount = subtotal -> new BigDecimal("50.00");
        assertEquals(new BigDecimal("800.00"), new CheckoutService(fixedDiscount).checkout(cart).total());
        assertEquals(new BigDecimal("850.00"), new CheckoutService(new NoDiscount()).checkout(cart).total());
    }

    @Test
    void monetaryRoundingUsesHalfUp() {
        Cart cart = new Cart();
        cart.add(new Product("P2", "Small item", new BigDecimal("0.05")), 1);
        Order order = new CheckoutService(new PercentageDiscount(new BigDecimal("10"))).checkout(cart);
        assertEquals(new BigDecimal("0.01"), order.discount());
        assertEquals(new BigDecimal("0.04"), order.total());
    }

    @Test
    void checkoutRejectsEmptyCartAndInvalidDiscounts() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> new CheckoutService(new NoDiscount()).checkout(cart));
        cart.add(product, 1);
        for (BigDecimal invalid : new BigDecimal[]{new BigDecimal("-1"), new BigDecimal("851")}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new CheckoutService(subtotal -> invalid).checkout(cart));
        }
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(new BigDecimal("101")));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(new BigDecimal("-1")));
    }

    @Test
    void readerSeesProductsAddedThroughEditor() {
        InMemoryProductCatalog storage = new InMemoryProductCatalog();
        ProductEditor editor = storage;
        ProductCatalog reader = storage;
        editor.save(product);
        assertEquals(product, reader.findById("P1").orElseThrow());
        assertTrue(reader.findById("unknown").isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> reader.findAll().clear());
    }

    @Test
    void consoleRecoversFromBadInputChecksOutAndClearsCart() {
        InMemoryProductCatalog catalog = new InMemoryProductCatalog();
        catalog.save(product);
        ConsoleShop shop = new ConsoleShop(catalog,
                new CheckoutService(new PercentageDiscount(new BigDecimal("10"))), new ReceiptPrinter());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        shop.run(new Scanner("2\nunknown\n2\nP1\ninvalid\n2\nP1\n2\n5\n3\n0\n"), new PrintStream(output));
        String text = output.toString();
        assertTrue(text.contains("Unknown product ID."));
        assertTrue(text.contains("valid whole-number quantity"));
        assertTrue(text.contains("Total:    1530.00 MDL"));
        assertTrue(text.contains("Your cart is empty."));
        assertTrue(text.contains("Goodbye!"));
    }

    @Test
    void consoleHandlesEndOfInputDuringPrompt() {
        ConsoleShop shop = new ConsoleShop(new InMemoryProductCatalog(),
                new CheckoutService(new NoDiscount()), new ReceiptPrinter());
        assertDoesNotThrow(() -> shop.run(new Scanner("2\n"), new PrintStream(new ByteArrayOutputStream())));
    }
}
