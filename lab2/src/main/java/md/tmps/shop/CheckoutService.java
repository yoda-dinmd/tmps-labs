package md.tmps.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// Factory Method: subclasses choose the policy used by the shared checkout flow.
public abstract class CheckoutService {
    protected abstract DiscountPolicy createDiscountPolicy();

    public Order checkout(Cart cart) {
        List<CartItem> items = cart.items();
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }
        BigDecimal subtotal = items.stream().map(CartItem::subtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
        DiscountPolicy discountPolicy = createDiscountPolicy();
        BigDecimal discount = discountPolicy.discountFor(subtotal);
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
