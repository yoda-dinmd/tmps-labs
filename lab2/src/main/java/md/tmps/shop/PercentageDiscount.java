package md.tmps.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageDiscount implements DiscountPolicy {
    private final BigDecimal percentage;

    public PercentageDiscount(BigDecimal percentage) {
        if (percentage == null || percentage.signum() < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }
        this.percentage = percentage;
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return subtotal.multiply(percentage).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }
}
