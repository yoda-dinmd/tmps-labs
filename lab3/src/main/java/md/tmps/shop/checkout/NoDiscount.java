package md.tmps.shop.checkout;


import java.math.BigDecimal;

public final class NoDiscount implements DiscountPolicy {
    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return BigDecimal.ZERO;
    }
}
