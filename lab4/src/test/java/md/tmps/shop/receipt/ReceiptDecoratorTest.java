package md.tmps.shop.receipt;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiptDecoratorTest {
    private final Receipt base = new ReceiptPrinter();

    @Test
    void itemCountAddsTheTotalQuantityAndPreservesTheBaseReceipt() {
        Order order = order();

        assertEquals(render(base, order) + "Items purchased: 5" + System.lineSeparator(),
                render(new ItemCountReceipt(base), order));
    }

    @Test
    void bothDecoratorsCanStackInEitherOrder() {
        Order order = order();
        String original = render(base, order);
        String count = "Items purchased: 5" + System.lineSeparator();
        String note = "Keep this receipt for returns." + System.lineSeparator();

        assertEquals(original + count + note, render(new ReturnNoteReceipt(new ItemCountReceipt(base)), order));
        assertEquals(original + note + count, render(new ItemCountReceipt(new ReturnNoteReceipt(base)), order));
    }

    private Order order() {
        Cart cart = new Cart();
        cart.add(new Product("P2", "Wireless mouse", new BigDecimal("350.00")), 2);
        cart.add(new Product("P3", "USB-C cable", new BigDecimal("120.00")), 3);
        return new CheckoutService(new NoDiscountStrategy()).checkout(cart);
    }

    private String render(Receipt receipt, Order order) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(buffer, false, StandardCharsets.UTF_8)) {
            receipt.print(order, out);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
