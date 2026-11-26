package md.tmps.shop.strategy;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

// Context: delegates discount calculation to a runtime-swappable DiscountStrategy.
public final class CheckoutService {
    private DiscountStrategy discountStrategy;

    public CheckoutService(DiscountStrategy discountStrategy) {
        this.discountStrategy = Objects.requireNonNull(discountStrategy, "discountStrategy");
    }

    public DiscountStrategy discountStrategy() {
        return discountStrategy;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = Objects.requireNonNull(discountStrategy, "discountStrategy");
    }

    public Order checkout(Cart cart) {
        List<CartItem> items = cart.items();
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }
        BigDecimal subtotal = items.stream()
                .map(CartItem::subtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
        BigDecimal discount = discountStrategy.discountFor(subtotal);
        if (discount == null || discount.signum() < 0 || discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount must be between zero and the subtotal.");
        }
        discount = discount.setScale(2, RoundingMode.HALF_UP);
        return new Order.Builder()
                .items(items)
                .subtotal(subtotal)
                .discount(discount)
                .build();
    }
}
