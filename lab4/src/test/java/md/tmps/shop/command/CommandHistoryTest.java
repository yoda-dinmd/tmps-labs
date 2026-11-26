package md.tmps.shop.command;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Product;
import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.DiscountStrategy;
import md.tmps.shop.strategy.NoDiscountStrategy;
import md.tmps.shop.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHistoryTest {
    private final Product mouse = new Product("P2", "Wireless mouse", new BigDecimal("350.00"));
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));

    @Test
    void addAndRemoveCommandsRestoreOriginalCartStateOnUndo() {
        Cart cart = new Cart();
        CommandHistory history = new CommandHistory();

        assertTrue(history.execute(new AddToCartCommand(cart, cable, 2)));
        assertTrue(history.execute(new AddToCartCommand(cart, mouse, 1)));
        // Requesting removal of 5 cables when only 2 exist removes 2 and must restore 2 on undo.
        assertTrue(history.execute(new RemoveFromCartCommand(cart, "P3", 5)));
        assertEquals(List.of(new CartItem(mouse, 1)), cart.items());

        // Removing a missing product is not pushed onto history.
        assertFalse(history.execute(new RemoveFromCartCommand(cart, "P99", 1)));

        assertEquals(Optional.of("Remove USB-C cable x2"), history.undo());
        assertEquals(List.of(new CartItem(mouse, 1), new CartItem(cable, 2)), cart.items());

        assertEquals(Optional.of("Add Wireless mouse x1"), history.undo());
        assertEquals(List.of(new CartItem(cable, 2)), cart.items());

        assertEquals(Optional.of("Add USB-C cable x2"), history.undo());
        assertTrue(cart.items().isEmpty());
        assertEquals(Optional.empty(), history.undo());
    }

    @Test
    void setDiscountStrategyCommandRestoresPreviousStrategyOnUndo() {
        DiscountStrategy initial = new NoDiscountStrategy();
        DiscountStrategy promo = new PercentageDiscountStrategy(new BigDecimal("10"));
        CheckoutService checkout = new CheckoutService(initial);
        CommandHistory history = new CommandHistory();

        assertTrue(history.execute(new SetDiscountStrategyCommand(checkout, promo)));
        assertSame(promo, checkout.discountStrategy());

        assertEquals(Optional.of("Set discount to 10% off"), history.undo());
        assertSame(initial, checkout.discountStrategy());
        assertEquals(Optional.empty(), history.undo());
    }
}
