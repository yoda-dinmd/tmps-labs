package md.tmps.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

// SRP: calculates an order. OCP: depends on the policy contract, not policy types.
public final class CheckoutService {
    private final DiscountPolicy discountPolicy;

    public CheckoutService(DiscountPolicy discountPolicy) {
        this.discountPolicy = Objects.requireNonNull(discountPolicy);
    }

    public Order checkout(Cart cart) {
        List<CartItem> items = cart.items();
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }
        BigDecimal subtotal = items.stream().map(CartItem::subtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
        BigDecimal discount = discountPolicy.discountFor(subtotal);
        if (discount == null || discount.signum() < 0 || discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount must be between zero and the subtotal.");
        }
        discount = discount.setScale(2, RoundingMode.HALF_UP);
        return new Order(items, subtotal, discount, subtotal.subtract(discount));
    }
}
